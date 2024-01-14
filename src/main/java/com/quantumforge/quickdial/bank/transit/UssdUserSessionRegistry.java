package com.quantumforge.quickdial.bank.transit;

import com.quantumforge.quickdial.session.UssdSession;

public interface UssdUserSessionRegistry {

    UssdSession getSession(String sessionId, boolean createIfAbsent);

    UssdSession getSession(String sessionId);

    void invalidateSession(UssdSession session);
}
