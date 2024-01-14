package com.quantumforge.quickdial.interceptor.impl;


import com.quantumforge.quickdial.annotation.UssdSubMenuHandler;
import com.quantumforge.quickdial.bootstrap.CommonUssdConfigProperties;
import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.exception.EmptyUssdUserSessionExecutionContextNavigableStackException;
import com.quantumforge.quickdial.interceptor.UssdSpecialInputInterceptor;
import com.quantumforge.quickdial.interceptor.UssdUserExecutionContextInterceptionResult;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = CommonUssdConfigProperties.class)
public class UssdGoBackSpecialInputInterceptor implements UssdSpecialInputInterceptor {

    private final CommonUssdConfigProperties ussdConfigProperties;

    @Override
    public UssdUserExecutionContextInterceptionResult intercept(UssdUserExecutionContext incomingExecutionContext, UssdSession ussdSession) {
        UssdUserExecutionContextInterceptionResult interceptionResult = new UssdUserExecutionContextInterceptionResult();
        UssdUserExecutionContext previousUserExecutionContext;
        if(supportsGoBackOperation(incomingExecutionContext.getInput(), incomingExecutionContext)){
            previousUserExecutionContext = ussdSession.getExecutionContext().getPreviousElement();
            if(Objects.isNull(previousUserExecutionContext)){
                throw new EmptyUssdUserSessionExecutionContextNavigableStackException(ussdSession.getSessionId());
            }
            interceptionResult.setIntercepted(true);
            interceptionResult.setResultingContext(previousUserExecutionContext);
        }else{
            interceptionResult.setResultingContext(null);
            interceptionResult.setIntercepted(false);
        }
        return interceptionResult;
    }

    @Override
    public int order() {
        return 2;
    }

    private boolean supportsGoBackOperation(String input, UssdUserExecutionContext context){
        boolean defaultGoBackOption = ussdConfigProperties.getGoBackOption().equalsIgnoreCase(input);
        UssdSubMenuHandler subMenuHandler = context.getExecutionContext().getInvocableMethod().getAnnotation(UssdSubMenuHandler.class);
        boolean relaxBackwardNavigation = Objects.nonNull(subMenuHandler) && subMenuHandler.relaxBackwardNavigation();
        return defaultGoBackOption && !relaxBackwardNavigation;
    }
}
