package com.quantumforge.quickdial.context.factory;

import com.quantumforge.quickdial.context.UssdExecutionContext;
import com.quantumforge.quickdial.session.UssdSession;

public interface UssdContextDataProviderFactory {
    String provideContextData(String input,  UssdExecutionContext incomingContext, UssdSession session);
}
