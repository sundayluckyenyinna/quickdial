package com.quantumforge.quickdial.simulation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.quantumforge.quickdial.bank.global.ApplicationItem;
import com.quantumforge.quickdial.bank.global.ApplicationStore;
import com.quantumforge.quickdial.bank.transit.UssdMappingRegistry;
import com.quantumforge.quickdial.bootstrap.CommonUssdConfigProperties;
import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.context.UserUssdContext;
import com.quantumforge.quickdial.context.UssdExecutionContext;
import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.messaging.template.strut.Message;
import com.quantumforge.quickdial.messaging.template.strut.MessageDocuments;
import com.quantumforge.quickdial.session.BackwardNavigableList;
import com.quantumforge.quickdial.session.UssdSession;
import com.quantumforge.quickdial.util.GeneralUtils;
import com.quantumforge.quickdial.util.QuickDialUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties({ CommonUssdConfigProperties.class })
public class BasicUssdExecutionSimulation implements UssdExecutionSimulation{

    private final ApplicationStore applicationStore;
    private final UssdMappingRegistry ussdMappingRegistry;
    private final CommonUssdConfigProperties ussdConfigProperties;

    @Override
    public UssdUserExecutionContext basicSimulation(BasicSimulationRequest request) {
        String ussdCode = request.getUssdCode();
        UssdSession session = request.getSession();
        UserUssdContext context = request.getContext();

        // Check if the context already present in the session's navigation context
        BackwardNavigableList<UssdUserExecutionContext> executionContexts = session.getExecutionContextChain();
        UssdUserExecutionContext existingContext = executionContexts.stream()
                .filter(ussdUserExecutionContext -> ussdUserExecutionContext.getUssdCode().equalsIgnoreCase(ussdCode))
                .findFirst()
                .orElse(null);
        if(Objects.nonNull(existingContext)){
            return existingContext;
        }

        // Build a simulated user ussd context
        MessageDocuments messageDocuments = applicationStore.getItem(ApplicationItem.MESSAGE_DOCUMENTS.name(), MessageDocuments.class);
        String[] messageIdToken = request.getDocumentMessageId().split(StringValues.FORWARD_SLASH);
        Message message = messageDocuments.getMessageDocumentByQualifiedNameAndMessageId(messageIdToken[0], messageIdToken[1]);
        UssdExecutionContext ussdExecutionContext = ussdMappingRegistry.getMatchingUssdExecutionContextForMapping(ussdCode);
        ContextInputDataToken token = getContextDataAndInputFromUssdCode(ussdCode, context);
        UssdUserExecutionContext newUserExecutionContext = UssdUserExecutionContext.builder()
                .executionContext(ussdExecutionContext)
                .contextData(token.getContextData())
                .ussdCode(ussdCode)
                .input(token.getInput())
                .msisdn(context.getMsisdn())
                .telco(context.getTelco())
                .prefix(context.getPrefix())
                .invocationType(ussdExecutionContext.getMenuHandler().type())
                .isStartingSession(context.isStartingSession())
                .isShortCodeContext(context.isShortCodeContext())
                .resultingMessage(message)
                .build();
        if(request.isAttachToSession()){
            session.updateUserUssdNavigationContext(newUserExecutionContext);
        }
        return newUserExecutionContext;
    }

    private ContextInputDataToken getContextDataAndInputFromUssdCode(String ussdCode, UserUssdContext context){
        String prefix = context.getPrefix();
        String baseUssdCodeWithoutHash = ussdConfigProperties.getBaseUssdCode().replace(QuickDialUtil.sProperties.getEndDelimiter(), StringValues.EMPTY_STRING);
        List<String> tokensWithoutBaseCode = QuickDialUtil.getStaticTokensBetweenDelimiters(ussdCode.replace(baseUssdCodeWithoutHash, StringValues.EMPTY_STRING))
                .stream()
                .filter(string -> Objects.nonNull(string) && !string.trim().isEmpty())
                .filter(string -> !string.equalsIgnoreCase(ussdConfigProperties.getBaseUssdCode()))
                .toList();
        tokensWithoutBaseCode = new ArrayList<>(tokensWithoutBaseCode);
        if(!GeneralUtils.isNullOrEmpty(prefix) && !tokensWithoutBaseCode.contains(prefix)){
            if(tokensWithoutBaseCode.size() >= 3){
                tokensWithoutBaseCode.set(2, prefix.trim());
            }else {
                tokensWithoutBaseCode.add(2, prefix.trim());
            }
        }
        tokensWithoutBaseCode.add(0, context.getMsisdn());
        tokensWithoutBaseCode.add(1, context.getTelco());
        String chainedTokens = QuickDialUtil.staticApplicationChain(tokensWithoutBaseCode);
        String input = tokensWithoutBaseCode.get(tokensWithoutBaseCode.size() - 1);
        return ContextInputDataToken.builder()
                .contextData(chainedTokens)
                .input(input)
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ContextInputDataToken{
        private String contextData;
        private String input;
    }
}
