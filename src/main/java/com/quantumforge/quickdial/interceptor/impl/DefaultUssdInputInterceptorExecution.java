package com.quantumforge.quickdial.interceptor.impl;

import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.interceptor.*;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DefaultUssdInputInterceptorExecution implements UssdInputInterceptorExecution {

    private final List<UssdInputValidationInterceptor> inputValidationInterceptors;
    private final List<UssdSpecialInputInterceptor> specialInputInterceptors;
    private final List<UssdInputParamValidationInterceptor> inputParamValidationInterceptors;

    @Override
    public UssdUserExecutionContextInterceptionResult checkSpecialInputInterception(UssdUserExecutionContext incomingContext, UssdSession session){
        return specialInputInterceptors.stream()
                .sorted(Comparator.comparingInt(UssdSpecialInputInterceptor::order))
                .map(interceptor -> interceptor.intercept(incomingContext, session))
                .filter(UssdUserExecutionContextInterceptionResult::isIntercepted)
                .findFirst()
                .orElseGet(UssdUserExecutionContextInterceptionResult::getNoInterceptionInstance);
    }

    @Override
    public UssdUserExecutionContextInterceptionResult checkValidInputInterception(String incomingInput, UssdSession session) {
        return inputValidationInterceptors.stream()
                .sorted(Comparator.comparingInt(UssdInputValidationInterceptor::order))
                .map(interceptor -> interceptor.intercept(incomingInput, session))
                .filter(UssdUserExecutionContextInterceptionResult::isIntercepted)
                .findFirst()
                .orElseGet(UssdUserExecutionContextInterceptionResult::getNoInterceptionInstance);
    }

    @Override
    public UssdUserExecutionContextInterceptionResult checkValidInputParamInterception(String incomingInput, UssdSession session) {
        return inputParamValidationInterceptors.stream()
                .sorted(Comparator.comparingInt(UssdInputParamValidationInterceptor::order))
                .map(interceptor -> interceptor.intercept(incomingInput, session))
                .filter(UssdUserExecutionContextInterceptionResult::isIntercepted)
                .findFirst()
                .orElseGet(UssdUserExecutionContextInterceptionResult::getNoInterceptionInstance);
    }
}
