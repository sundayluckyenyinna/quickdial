package com.quantumforge.quickdial.interceptor;

import com.quantumforge.quickdial.annotation.UssdParam;
import com.quantumforge.quickdial.annotation.Valid;
import com.quantumforge.quickdial.bootstrap.CommonUssdConfigProperties;
import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.context.UssdExecutionContext;
import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.execution.result.UssdRedirectConfigProperties;
import com.quantumforge.quickdial.messaging.builder.MessageSourceDocumentBuilder;
import com.quantumforge.quickdial.messaging.template.strut.Message;
import com.quantumforge.quickdial.messaging.template.strut.MessageLine;
import com.quantumforge.quickdial.session.UssdModel;
import com.quantumforge.quickdial.session.UssdSession;
import com.quantumforge.quickdial.util.GeneralUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface UssdInputValidationInterceptor {
    String TEMPLATE_ERROR_KEY = "isRedirectForOptionValidationError";
    String TEMPLATE_INPUT_PARAM_ERROR_KEY = "isRedirectForParamValidationError";
    String ERROR_RETRY_ATTEMPT_LEFT = "errorRetryAttemptLeft";
    String ERROR_RETRY_SUFFIX = "errorRetrySuffix";
    String WRONG_INPUT_REDIRECT_COUNT = "WRONG_INPUT_REDIRECT_COUNT";


    UssdUserExecutionContextInterceptionResult intercept(String incomingInput, UssdSession ussdSession);

    UssdRedirectConfigProperties getRedirectionProperties();
    CommonUssdConfigProperties getCommonUssdConfigProperties();
    int order();


    default void setFocusForRedirection(UssdSession session){
        String redirectionId = getRedirectionProperties().getInputValidationErrorRedirectReference();
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

    default void updateShowErrorMessageCommand(UssdModel ussdModel, boolean value, int retryLeft){
        if(getRedirectionProperties().isEnableAutomaticErrorRedirectionMessage()){
            ussdModel.addObject(TEMPLATE_ERROR_KEY, value);
            ussdModel.addObject(ERROR_RETRY_ATTEMPT_LEFT, String.valueOf(retryLeft));
            ussdModel.addObject(ERROR_RETRY_SUFFIX, GeneralUtils.getPluralisedRetry(retryLeft));
        }
    }

    default void updateShowInputParamErrorMessageCommand(UssdModel ussdModel, String errorMessage, boolean value, int retryLeft){
        if(getRedirectionProperties().isEnableAutomaticErrorRedirectionMessage()){
            ussdModel.addObject(TEMPLATE_INPUT_PARAM_ERROR_KEY, value);
            ussdModel.addObject(ERROR_RETRY_ATTEMPT_LEFT, String.valueOf(retryLeft));
            ussdModel.addObject(ERROR_RETRY_SUFFIX, GeneralUtils.getPluralisedRetry(retryLeft));
            ussdModel.addObject(MessageSourceDocumentBuilder.INPUT_VALIDATION_ERROR_PLACE_HOLDER, errorMessage);
        }
    }

    default List<String> getOptionsInMessage(Message message){
        return message.getLines()
                .stream()
                .map(MessageLine::getOption)
                .filter(option -> Objects.nonNull(option) && !option.trim().isEmpty())
                .filter(this::isNotSpecialInput)
                .map(option -> {
                    if(option.endsWith(StringValues.DOT)){
                        return option.substring(0, option.lastIndexOf(StringValues.DOT));
                    }
                    return option.trim();
                }).collect(Collectors.toList());
    }

    default boolean isNotSpecialInput(String input){
        return !Arrays.asList(getCommonUssdConfigProperties().getGoBackOption(), getCommonUssdConfigProperties().getGoForwardOption()).contains(input);
    }

    default boolean isInvalidParam(Parameter parameter, String incomingInput){
        boolean hasValidationAnnotation = hasValidationAnnotation(parameter);
        if(hasValidationAnnotation){
            Valid validAnn = parameter.getDeclaredAnnotation(Valid.class);
            String regexPattern = validAnn.pattern();
            return !GeneralUtils.isNullOrEmpty(regexPattern) && !Pattern.matches(regexPattern, incomingInput);
        }else{
            return false;
        }
    }

    default int getTrialTimes(UssdSession ussdSession){
        int trials;
        Object redirectCount = ussdSession.getSessionData().getAttribute(WRONG_INPUT_REDIRECT_COUNT);
        if(Objects.nonNull(redirectCount)){
            int redirectCountInt = Integer.parseInt(String.valueOf(redirectCount));
            trials = redirectCountInt + 1;
            ussdSession.getSessionData().keepAttribute(WRONG_INPUT_REDIRECT_COUNT, trials);
        }else{
            trials = 1;
            ussdSession.getSessionData().keepAttribute(WRONG_INPUT_REDIRECT_COUNT, trials);
        }
        return trials;
    }

    default boolean hasValidationAnnotation(Parameter parameter){
        boolean hasValidationParam = parameter.isAnnotationPresent(Valid.class) && !parameter.getDeclaredAnnotation(Valid.class).pattern().isEmpty();
        boolean isUssdParamAnnotationPresent = parameter.isAnnotationPresent(UssdParam.class);
        return isUssdParamAnnotationPresent && hasValidationParam;
    }
}
