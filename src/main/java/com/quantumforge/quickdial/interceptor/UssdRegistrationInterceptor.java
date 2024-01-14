package com.quantumforge.quickdial.interceptor;

import com.quantumforge.quickdial.payload.QuickDialPayload;
import com.quantumforge.quickdial.session.UssdSession;

public interface UssdRegistrationInterceptor {

    UssdSession registerSession(String sessionId);
}
