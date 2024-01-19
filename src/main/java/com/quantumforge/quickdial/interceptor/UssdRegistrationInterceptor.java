package com.quantumforge.quickdial.interceptor;

import com.quantumforge.quickdial.session.UssdSession;

public interface UssdRegistrationInterceptor {

    UssdSession registerOrRetrieveSession(String sessionId);
}
