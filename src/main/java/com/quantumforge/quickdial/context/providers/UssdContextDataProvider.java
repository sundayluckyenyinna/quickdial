package com.quantumforge.quickdial.context.providers;

import com.quantumforge.quickdial.context.UssdExecutableType;
import com.quantumforge.quickdial.context.UssdExecutionContext;
import com.quantumforge.quickdial.session.UssdSession;

public interface UssdContextDataProvider {

    boolean supports(UssdExecutableType  executableType);
    String provide(String input,  UssdExecutionContext incomingContext, UssdSession session);
}
