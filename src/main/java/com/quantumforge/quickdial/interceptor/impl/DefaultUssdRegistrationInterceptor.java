package com.quantumforge.quickdial.interceptor.impl;

import com.quantumforge.quickdial.bank.transit.UssdUserSessionRegistry;
import com.quantumforge.quickdial.interceptor.UssdRegistrationInterceptor;
import com.quantumforge.quickdial.payload.QuickDialPayload;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DefaultUssdRegistrationInterceptor implements UssdRegistrationInterceptor {

    private final UssdUserSessionRegistry sessionRegistry;

    @Override
    public UssdSession registerSession(String sessionId) {
        return sessionRegistry.getSession(sessionId, true);
    }
}
