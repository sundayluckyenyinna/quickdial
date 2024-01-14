package com.quantumforge.quickdial.interceptor.impl;

import com.quantumforge.quickdial.annotation.UssdGroupMapping;
import com.quantumforge.quickdial.bank.transit.UssdMappingRegistry;
import com.quantumforge.quickdial.bank.transit.impl.SimpleUssdMappingRegistry;
import com.quantumforge.quickdial.bootstrap.CommonUssdConfigProperties;
import com.quantumforge.quickdial.context.*;
import com.quantumforge.quickdial.exception.EmptyUssdUserSessionExecutionContextNavigableStackException;
import com.quantumforge.quickdial.interceptor.UssdSpecialInputInterceptor;
import com.quantumforge.quickdial.interceptor.UssdUserExecutionContextInterceptionResult;
import com.quantumforge.quickdial.session.BackwardNavigableList;
import com.quantumforge.quickdial.session.UssdSession;
import com.quantumforge.quickdial.util.QuickDialUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = CommonUssdConfigProperties.class)
public class UssdGoForwardSpecialInputInterceptor implements UssdSpecialInputInterceptor {

    private final QuickDialUtil quickDialUtil;
    private final UssdMappingRegistry ussdMappingRegistry;
    private final CommonUssdConfigProperties ussdConfigProperties;

    @Override
    public UssdUserExecutionContextInterceptionResult intercept(UssdUserExecutionContext incomingExecutionContext, UssdSession ussdSession) {
        UssdUserExecutionContextInterceptionResult interceptionResult = new UssdUserExecutionContextInterceptionResult();
        UssdUserExecutionContext nextUserExecutionContext;

        // Check if the current sole or group supports forward navigation
        if(supportsGoForwardNavigation(incomingExecutionContext.getInput(), incomingExecutionContext)){
            BackwardNavigableList<UssdUserExecutionContext> navigableList = ussdSession.getExecutionContext();

            // There is a next context at the front of the current context and the current context is a sole context
            if(navigableList.hasNext() && navigableList.getCurrentElement().isSole()){
                nextUserExecutionContext = navigableList.getNextElement();
                interceptionResult.setIntercepted(true);
                interceptionResult.setResultingContext(nextUserExecutionContext);
                return interceptionResult;
            }
            else if(navigableList.hasNext() && navigableList.getCurrentElement().isInGroup()){
                String groupId = incomingExecutionContext.getExecutionContext().getGroupMapping().id();
                GroupUssdExecutableContextWrapper contextWrapper = SimpleUssdMappingRegistry.getGroupExecutableByGroupId(groupId);
                UssdUserExecutionContext possibleExecutionInGroup = ussdSession.getLatestUssdUserExecutionContextInGroup(groupId);
                UssdExecutionContext context = Objects.isNull(possibleExecutionInGroup) ? contextWrapper.getFirst() : contextWrapper.getNextAfter(possibleExecutionInGroup.getExecutionContext());
                if(contextWrapper.isLastInGroup(possibleExecutionInGroup.getExecutionContext())){
                    nextUserExecutionContext = navigableList.getNextElement();
                    interceptionResult.setIntercepted(true);
                    interceptionResult.setResultingContext(nextUserExecutionContext);
                    return interceptionResult;
                }

                // Create another UssdUserContext with similar context data and add it to the session.
                UssdUserExecutionContext ussdUserExecutionContext = getSimilarGroupUssdUserExecutionContext(incomingExecutionContext, context);
                ussdSession.updateUserUssdNavigationContext(ussdUserExecutionContext);
                interceptionResult.setIntercepted(true);
                interceptionResult.setResultingContext(ussdUserExecutionContext);
                return interceptionResult;
            }
            nextUserExecutionContext = ussdSession.getExecutionContext().getNextElement(); // This is actually the last element
            if(Objects.isNull(nextUserExecutionContext)){                                 // The navigable list is actually empty
                throw new EmptyUssdUserSessionExecutionContextNavigableStackException(ussdSession.getSessionId());
            }
            return getResolvedSoleOrGroupContextInterceptionResult(nextUserExecutionContext, ussdSession);
        }
        else{
            // Check if the current context is a group and that it is relaxing the forward option.
            UssdUserExecutionContext currentContext = ussdSession.getExecutionContext().getCurrentElement();
            if(Objects.nonNull(currentContext) &&
                    Objects.nonNull(currentContext.getExecutionContext().getGroupMapping()) &&
                    currentContext.getExecutionContext().getGroupMapping().relaxForwardNavigation()){

                String fullUssdCode = quickDialUtil.extendUssdCode(currentContext.getUssdCode(), incomingExecutionContext.getInput());
                String contextData = quickDialUtil.applicationChain(currentContext.getContextData(), incomingExecutionContext.getInput());

                UssdExecutionContext executionContext = ussdMappingRegistry.getMatchingUssdExecutionContextForMapping(fullUssdCode);

                UssdUserExecutionContext ussdUserExecutionContext = new UssdUserExecutionContext();
                ussdUserExecutionContext.setInput(incomingExecutionContext.getInput());
                ussdUserExecutionContext.setUssdCode(fullUssdCode);
                ussdUserExecutionContext.setContextData(contextData);
                ussdUserExecutionContext.setExecutionContext(executionContext);

                ussdSession.updateUserUssdNavigationContext(ussdUserExecutionContext);
                interceptionResult.setIntercepted(true);
                interceptionResult.setResultingContext(ussdUserExecutionContext);
            }
            else {
                interceptionResult.setIntercepted(false);
                interceptionResult.setResultingContext(null);
            }
        }
        return interceptionResult;
    }

    @Override
    public int order() {
        return 3;
    }

    private UssdUserExecutionContextInterceptionResult getResolvedSoleOrGroupContextInterceptionResult(UssdUserExecutionContext lastContextInUserNavigation, UssdSession session){
        UssdUserExecutionContextInterceptionResult result = new UssdUserExecutionContextInterceptionResult();
        result.setIntercepted(true);
        if (lastContextInUserNavigation.isInGroup()){
            String groupId = lastContextInUserNavigation.getExecutionContext().getGroupMapping().id();
            GroupUssdExecutableContextWrapper contextWrapper = SimpleUssdMappingRegistry.getGroupExecutableByGroupId(groupId);
            UssdUserExecutionContext possibleExecutionInGroup = session.getLatestUssdUserExecutionContextInGroup(groupId);
            UssdExecutionContext context = Objects.isNull(possibleExecutionInGroup) ? contextWrapper.getFirst() : contextWrapper.getNextAfter(possibleExecutionInGroup.getExecutionContext());

            // Create another UssdUserContext with similar context data and add it to the session.
            UssdUserExecutionContext ussdUserExecutionContext = getSimilarGroupUssdUserExecutionContext(lastContextInUserNavigation, context);
            result.setResultingContext(ussdUserExecutionContext);
            session.updateUserUssdNavigationContext(ussdUserExecutionContext);
        }
        else if(lastContextInUserNavigation.isSole()){
            result.setResultingContext(lastContextInUserNavigation);
            session.updateUserUssdNavigationContext(lastContextInUserNavigation);
        }
        else {
            throw new IllegalArgumentException(String.format("Invalid UssdExecutable type: %s", lastContextInUserNavigation.getExecutionContext().getParentExecutionType()));
        }
        return result;
    }

    private static UssdUserExecutionContext getSimilarGroupUssdUserExecutionContext(UssdUserExecutionContext lastContextInUserNavigation, UssdExecutionContext context) {
        UssdUserExecutionContext ussdUserExecutionContext = new UssdUserExecutionContext();
        ussdUserExecutionContext.setExecutionContext(context);
        ussdUserExecutionContext.setContextData(lastContextInUserNavigation.getContextData());
        ussdUserExecutionContext.setUssdCode(lastContextInUserNavigation.getUssdCode());
        ussdUserExecutionContext.setInput(lastContextInUserNavigation.getInput());
        ussdUserExecutionContext.setMsisdn(lastContextInUserNavigation.getMsisdn());
        ussdUserExecutionContext.setTelco(lastContextInUserNavigation.getTelco());
        ussdUserExecutionContext.setInvocationType(lastContextInUserNavigation.getInvocationType());
        ussdUserExecutionContext.setStartingSession(lastContextInUserNavigation.isStartingSession());
        ussdUserExecutionContext.setShortCodeContext(lastContextInUserNavigation.isShortCodeContext());
        ussdUserExecutionContext.setPrefix(lastContextInUserNavigation.getPrefix());
        return ussdUserExecutionContext;
    }


    private boolean supportsGoForwardNavigation(String input, UssdUserExecutionContext incomingExecutionContext){
        boolean defaultGoForward = ussdConfigProperties.getGoForwardOption().equalsIgnoreCase(input);
        UssdGroupMapping groupMapping = incomingExecutionContext.getExecutionContext().getGroupMapping();
        boolean relaxForwardNavigation = Objects.nonNull(groupMapping) && groupMapping.relaxForwardNavigation();
        return defaultGoForward && !relaxForwardNavigation;
    }
}
