package com.quantumforge.quickdial.interceptor;

import com.quantumforge.quickdial.session.UssdSession;

public interface UssdInputValidationInterceptor {

    UssdUserExecutionContextInterceptionResult intercept(String incomingInput, UssdSession ussdSession);
    int order();
}
