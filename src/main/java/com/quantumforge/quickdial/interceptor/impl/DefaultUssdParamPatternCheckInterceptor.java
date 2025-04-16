package com.quantumforge.quickdial.interceptor.impl;

import com.quantumforge.quickdial.annotation.UssdParam;
import com.quantumforge.quickdial.annotation.Valid;
import com.quantumforge.quickdial.bootstrap.CommonUssdConfigProperties;
import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.execution.result.UssdRedirectConfigProperties;
import com.quantumforge.quickdial.interceptor.UssdInputParamValidationInterceptor;
import com.quantumforge.quickdial.interceptor.UssdUserExecutionContextInterceptionResult;
import com.quantumforge.quickdial.session.UssdModel;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({CommonUssdConfigProperties.class, UssdRedirectConfigProperties.class})
public class DefaultUssdParamPatternCheckInterceptor implements UssdInputParamValidationInterceptor {

    private final UssdRedirectConfigProperties redirectConfigProperties;
    private final CommonUssdConfigProperties ussdConfigProperties;

    @Override
    public UssdUserExecutionContextInterceptionResult intercept(String incomingInput, UssdSession ussdSession) {
        UssdUserExecutionContextInterceptionResult result = new UssdUserExecutionContextInterceptionResult();

        UssdModel userUssdModel = ussdSession.getUssdModel();
        int maximumRetryTimes = ussdConfigProperties.getAcceptableInputTrialTimes();
        UssdUserExecutionContext currentContext = ussdSession.getExecutionContextChain().getCurrentElement();
        Method method = currentContext.getExecutionContext().getInvocableMethod();
        Parameter ussdMethodParameter = Arrays.stream(method.getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(UssdParam.class) && parameter.isAnnotationPresent(Valid.class) && !parameter.getDeclaredAnnotation(Valid.class).pattern().isEmpty())
                .findFirst()
                .orElse(null);
        boolean isParameterUssdParamAnnotationPresent = Objects.nonNull(ussdMethodParameter);
        if(isParameterUssdParamAnnotationPresent){
            boolean isParamViolationFound = isInvalidParam(ussdMethodParameter, incomingInput);
            if(isNotSpecialInput(incomingInput) && isParamViolationFound) {
                String errorMessage = ussdMethodParameter.getDeclaredAnnotation(Valid.class).message();
                if (Objects.nonNull(incomingInput)) {
                    int trialTimes = getTrialTimes(ussdSession);
                    if (trialTimes <= maximumRetryTimes) {  // retry same page for user
                        result.setIntercepted(true);
                        result.setResultingContext(ussdSession.getExecutionContextChain().getPreviousElement());
                        updateShowInputParamErrorMessageCommand(userUssdModel, errorMessage, true, maximumRetryTimes - trialTimes + 1);
                    } else {
                        updateShowInputParamErrorMessageCommand(userUssdModel, errorMessage, false, maximumRetryTimes);
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
                    updateShowInputParamErrorMessageCommand(userUssdModel, errorMessage,false, maximumRetryTimes);
                    ussdSession.getSessionData().keepAttribute(WRONG_INPUT_REDIRECT_COUNT, 0);
                }
            }else{
                updateShowInputParamErrorMessageCommand(userUssdModel, StringValues.EMPTY_STRING, false, maximumRetryTimes);
            }
        }
        else{
            updateShowInputParamErrorMessageCommand(userUssdModel, StringValues.EMPTY_STRING, false, maximumRetryTimes);
        }
        return result;
    }

    @Override
    public UssdRedirectConfigProperties getRedirectionProperties() {
        return redirectConfigProperties;
    }

    @Override
    public CommonUssdConfigProperties getCommonUssdConfigProperties() {
        return ussdConfigProperties;
    }

    @Override
    public int order() {
        return 1;
    }
}
