package com.quantumforge.quickdial.context;

import com.quantumforge.quickdial.annotation.UssdSubMenuHandler;
import com.quantumforge.quickdial.execution.provider.UssdInvocationType;
import com.quantumforge.quickdial.messaging.template.strut.Message;
import com.quantumforge.quickdial.util.QuickDialUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UssdUserExecutionContext {
    private UssdExecutionContext executionContext;
    private String contextData;
    private String ussdCode;
    private String input;
    private String msisdn;
    private String telco;
    private String prefix;
    private UssdInvocationType invocationType;
    private boolean isStartingSession;
    private boolean isShortCodeContext;
    private Message resultingMessage;

    public boolean isInGroup(){
        boolean parentIsGroupType = executionContext.getParentExecutionType() == UssdExecutableType.GROUP_EXECUTABLE;
        boolean isInGroup = Objects.nonNull(executionContext.getGroupMapping());
        return parentIsGroupType && isInGroup;
    }

    public boolean isSole(){
        boolean isSole = executionContext.getParentExecutionType() == UssdExecutableType.SOLE_EXECUTABLE;
        boolean hasNoGroup = Objects.isNull(executionContext.getGroupMapping());
        return isSole && hasNoGroup;
    }

    public boolean isRelaxMenuOptionCheck(){
        UssdSubMenuHandler subMenuHandler = getExecutionContext().getUssdSubMenuHandler();
        return Objects.nonNull(subMenuHandler) && subMenuHandler.relaxMenuOptionCheck();
    }

    public void updateInputAndCorrespondingContextData(String input){
        this.setInput(input);
        List<String> contextDataTokens = QuickDialUtil.getStaticTokensBetweenDelimiters(getContextData());
        contextDataTokens.set(contextDataTokens.size() - 1, input);
        String newContextData = QuickDialUtil.staticApplicationChain(contextDataTokens);
        this.setContextData(newContextData);
    }
}
