package com.quantumforge.quickdial.interceptor.impl;

import com.quantumforge.quickdial.bootstrap.CommonUssdConfigProperties;
import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.execution.result.UssdRedirectConfigProperties;
import com.quantumforge.quickdial.interceptor.UssdInputValidationInterceptor;
import com.quantumforge.quickdial.interceptor.UssdUserExecutionContextInterceptionResult;
import com.quantumforge.quickdial.messaging.template.strut.Message;
import com.quantumforge.quickdial.messaging.template.strut.MessageLine;
import com.quantumforge.quickdial.session.UssdModel;
import com.quantumforge.quickdial.session.UssdSession;
import com.quantumforge.quickdial.util.GeneralUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({CommonUssdConfigProperties.class, UssdRedirectConfigProperties.class})
public class DefaultOptionCheckInterceptor implements UssdInputValidationInterceptor {

    private final UssdRedirectConfigProperties redirectConfigProperties;
    private final CommonUssdConfigProperties ussdConfigProperties;

    private final static String REDIRECT_COUNT = "WRONG_INPUT_REDIRECT_COUNT";


    @Override
    public UssdUserExecutionContextInterceptionResult intercept(String incomingInput, UssdSession ussdSession) {
        UssdUserExecutionContextInterceptionResult result = new UssdUserExecutionContextInterceptionResult();

        UssdModel userUssdModel = ussdSession.getUssdModel();
        int maximumRetryTimes = ussdConfigProperties.getAcceptableInputTrialTimes();
        UssdUserExecutionContext currentContext = ussdSession.getExecutionContextChain().getCurrentElement();
        if(Objects.nonNull(currentContext) && !currentContext.isRelaxMenuOptionCheck() && ussdConfigProperties.isEnableMenuOptionCheck()){
            Message message = currentContext.getResultingMessage();
            if(Objects.nonNull(message) && !isSpecialInput(incomingInput)) {
                List<String> optionsInMessage = getOptionsInMessage(message);
                if (Objects.nonNull(incomingInput) && !optionsInMessage.contains(incomingInput)) {
                    int trialTimes = getTrialTimes(ussdSession);
                    if (trialTimes <= maximumRetryTimes) {  // retry same page for user
                        result.setIntercepted(true);
                        result.setResultingContext(currentContext);
                        updateShowErrorMessageCommand(userUssdModel, true, maximumRetryTimes - trialTimes + 1);
                    } else {
                        updateShowErrorMessageCommand(userUssdModel, false, maximumRetryTimes);
                        if(ussdSession.getExecutionContextChain().size() > 1) { // flush all sessions and redirect to first page
                            setFocusForRedirection(ussdSession);
                            result.setIntercepted(true);
                            result.setResultingContext(ussdSession.getExecutionContextChain().getCurrentElement());
                        }
                        else if(ussdSession.getExecutionContextChain().size() == 1){
                            result.setIntercepted(true);
                            result.setResultingContext(ussdSession.getExecutionContextChain().getCurrentElement());
                        }
                    }
                }else { // Reset the trial to 0 since user has now entered correct input
                    updateShowErrorMessageCommand(userUssdModel, false, maximumRetryTimes);
                    ussdSession.getSessionData().keepAttribute(REDIRECT_COUNT, 0);
                }
            }else{
                updateShowErrorMessageCommand(userUssdModel, false, maximumRetryTimes);
            }
        }else{
            updateShowErrorMessageCommand(userUssdModel, false, maximumRetryTimes);
        }
        return result;
    }

    private void setFocusForRedirection(UssdSession session){
        String redirectionId = redirectConfigProperties.getInputValidationErrorRedirectReference();
        UssdUserExecutionContext currentContext = session.getExecutionContextChain().getCurrentElement();
        UssdUserExecutionContext focusableExecutionContext;
        if(GeneralUtils.isNullOrEmpty(redirectionId)){
            focusableExecutionContext = session.getExecutionContextChain().getFirstElement();
        }else{
            focusableExecutionContext = session.getUssdUserExecutionContextByContextId(redirectionId);
            if(session.isBefore(currentContext, focusableExecutionContext)){
                focusableExecutionContext = session.getExecutionContextChain().getCurrentElement();
            }
        }
        session.setFocusOnContext(focusableExecutionContext);
    }

    private void updateShowErrorMessageCommand(UssdModel ussdModel, boolean value, int retryLeft){
        if(redirectConfigProperties.isEnableAutomaticErrorRedirectionMessage()){
            ussdModel.addObject(TEMPLATE_ERROR_KEY, value);
            ussdModel.addObject(ERROR_RETRY_ATTEMPT_LEFT, String.valueOf(retryLeft));
            ussdModel.addObject(ERROR_RETRY_SUFFIX, GeneralUtils.getPluralisedRetry(retryLeft));
        }
    }

    private List<String> getOptionsInMessage(Message message){
        return message.getLines()
                .stream()
                .map(MessageLine::getOption)
                .filter(option -> Objects.nonNull(option) && !option.trim().isEmpty())
                .map(option -> {
                    if(option.endsWith(StringValues.DOT)){
                        return option.substring(0, option.lastIndexOf(StringValues.DOT));
                    }
                    return option.trim();
                }).collect(Collectors.toList());
    }

    private boolean isSpecialInput(String input){
        return Arrays.asList(ussdConfigProperties.getGoBackOption(), ussdConfigProperties.getGoForwardOption()).contains(input);
    }

    private int getTrialTimes(UssdSession ussdSession){
        int trials;
        Object redirectCount = ussdSession.getSessionData().getAttribute(REDIRECT_COUNT);
        if(Objects.nonNull(redirectCount)){
            int redirectCountInt = Integer.parseInt(String.valueOf(redirectCount));
            trials = redirectCountInt + 1;
            ussdSession.getSessionData().keepAttribute(REDIRECT_COUNT, trials);
        }else{
            trials = 1;
            ussdSession.getSessionData().keepAttribute(REDIRECT_COUNT, trials);
        }
        return trials;
    }

    @Override
    public int order() {
        return 1;
    }
}
