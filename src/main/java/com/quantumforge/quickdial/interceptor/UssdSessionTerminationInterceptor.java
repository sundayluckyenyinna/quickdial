package com.quantumforge.quickdial.interceptor;

import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.session.UssdSession;

public interface UssdSessionTerminationInterceptor {
    void intercept(UssdUserExecutionContext incomingContext, UssdSession ussdSession);
}
