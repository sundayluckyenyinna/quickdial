package com.quantumforge.quickdial.interceptor;

import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.session.UssdSession;

public interface UssdSpecialInputInterceptor {

    UssdUserExecutionContextInterceptionResult intercept(UssdUserExecutionContext incomingExecutionContext, UssdSession ussdSession);

    int order();
}
