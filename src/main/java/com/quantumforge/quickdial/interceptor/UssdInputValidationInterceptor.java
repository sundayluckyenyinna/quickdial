package com.quantumforge.quickdial.interceptor;

import com.quantumforge.quickdial.session.UssdSession;

public interface UssdInputValidationInterceptor {
    String TEMPLATE_ERROR_KEY = "isRedirectForOptionValidationError";
    String ERROR_RETRY_ATTEMPT_LEFT = "errorRetryAttemptLeft";
    String ERROR_RETRY_SUFFIX = "errorRetrySuffix";


    UssdUserExecutionContextInterceptionResult intercept(String incomingInput, UssdSession ussdSession);
    int order();
}
