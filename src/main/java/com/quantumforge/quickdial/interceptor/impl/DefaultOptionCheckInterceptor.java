package com.quantumforge.quickdial.interceptor.impl;

import com.quantumforge.quickdial.bootstrap.CommonUssdConfigProperties;
import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.interceptor.UssdInputValidationInterceptor;
import com.quantumforge.quickdial.interceptor.UssdSpecialInputInterceptor;
import com.quantumforge.quickdial.interceptor.UssdUserExecutionContextInterceptionResult;
import com.quantumforge.quickdial.messaging.template.strut.Message;
import com.quantumforge.quickdial.messaging.template.strut.MessageLine;
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
@EnableConfigurationProperties(CommonUssdConfigProperties.class)
public class DefaultOptionCheckInterceptor implements UssdInputValidationInterceptor {

    private final CommonUssdConfigProperties ussdConfigProperties;

    private final static String REDIRECT_COUNT = "WRONG_INPUT_REDIRECT_COUNT";


    @Override
    public UssdUserExecutionContextInterceptionResult intercept(String incomingInput, UssdSession ussdSession) {
        UssdUserExecutionContextInterceptionResult result = new UssdUserExecutionContextInterceptionResult();

        UssdUserExecutionContext currentContext = ussdSession.getExecutionContext().getCurrentElement();
        if(Objects.nonNull(currentContext) && ussdConfigProperties.isEnableMenuOptionCheck()){
            Message message = GeneralUtils.returnValueOrDefaultWith(currentContext.getResultingMessage(), ussdSession.getLatestMessage());
            if(Objects.nonNull(message) && !isSpecialInput(incomingInput)) {
                List<String> optionsInMessage = getOptionsInMessage(message);
                if (Objects.nonNull(incomingInput) && !optionsInMessage.contains(incomingInput)) {
                    int trialTimes = getTrialTimes(incomingInput, optionsInMessage, ussdSession);
                    if (trialTimes <= ussdConfigProperties.getAcceptableInputTrialTimes()) {  // retry same page for user
                        result.setIntercepted(true);
                        result.setResultingContext(currentContext);
                    } else {
                        if(ussdSession.getExecutionContext().size() > 1) { // flush all sessions and redirect to first page
                            ussdSession.flushAllSessionContextButAtIndex(0);
                            result.setIntercepted(true);
                            result.setResultingContext(ussdSession.getExecutionContext().getCurrentElement());
                        }
                        else if(ussdSession.getExecutionContext().size() == 1){
                            result.setIntercepted(true);
                            result.setResultingContext(ussdSession.getExecutionContext().getCurrentElement());
                        }
                    }
                }else { // Reset the trial to 0 since user has now entered correct input
                    ussdSession.getSessionData().keepAttribute(REDIRECT_COUNT, 0);
                }
            }
        }
        return result;
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

    private int getTrialTimes(String incomingInput, List<String> optionsInMessage, UssdSession ussdSession){
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
