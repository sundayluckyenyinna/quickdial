package com.quantumforge.quickdial.interceptor;

import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.session.UssdSession;

public interface UssdInputInterceptorExecution {
    UssdUserExecutionContextInterceptionResult checkSpecialInputInterception(UssdUserExecutionContext incomingContext, UssdSession session);
    UssdUserExecutionContextInterceptionResult checkValidInputInterception(String incomingInput, UssdSession session);
}
