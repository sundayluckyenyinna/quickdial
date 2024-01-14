package com.quantumforge.quickdial.interceptor.impl;

import com.quantumforge.quickdial.annotation.EndUssdSession;
import com.quantumforge.quickdial.bank.transit.UssdUserSessionRegistry;
import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.interceptor.UssdSessionTerminationInterceptor;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DefaultUssdSessionTerminationInterceptor implements UssdSessionTerminationInterceptor {

    private final UssdUserSessionRegistry ussdUserSessionRegistry;

    @Override
    public void intercept(UssdUserExecutionContext incomingContext, UssdSession ussdSession){
        if(incomingContext.getExecutionContext().getInvocableMethod().isAnnotationPresent(EndUssdSession.class)){
            ussdUserSessionRegistry.invalidateSession(ussdSession);
        }
    }
}
