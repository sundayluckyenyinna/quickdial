package com.quantumforge.quickdial.bank.transit.impl;

import com.quantumforge.quickdial.bank.transit.UssdUserSessionRegistry;
import com.quantumforge.quickdial.session.SessionData;
import com.quantumforge.quickdial.session.UssdModel;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleUssdUserSessionRegistry implements UssdUserSessionRegistry {

    private static final ConcurrentMap<String, UssdSession> SESSION_REGISTRY_LOG = new ConcurrentHashMap<>();

    @Override
    public UssdSession getSession(String sessionId, boolean createIfAbsent){
        if(isNullOrEmpty(sessionId)){
            throw new IllegalArgumentException("sessionId cannot be null or empty!");
        }
        UssdSession session = SESSION_REGISTRY_LOG.get(sessionId);
        if(Objects.isNull(session) && createIfAbsent){
            session = new UssdSession();
            session.setSessionId(sessionId);
            SESSION_REGISTRY_LOG.put(sessionId, session);
        }
        return session;
    }

    @Override
    public UssdSession getSession(String sessionId){
        return getSession(sessionId, true);
    }

    public static UssdSession getSessionStatically(String sessionId){
        return SESSION_REGISTRY_LOG.get(sessionId);
    }

    public void invalidateSession(String sessionId){
        UssdSession sessionToBeInvalidated = getSession(sessionId, false);
        if(!Objects.isNull(sessionToBeInvalidated)){
            SESSION_REGISTRY_LOG.remove(sessionId);
        }
    }

    @Override
    public void invalidateSession(UssdSession session){
        if(Objects.isNull(session)){
            throw new IllegalArgumentException("session to be invalidated cannot be null!");
        }
        invalidateSession(session.getSessionId());
    }

    private static boolean isNullOrEmpty(String value){
        return Objects.isNull(value) || value.trim().isEmpty();
    }
}
