package com.quantumforge.quickdial.bank.transit.impl;

import com.quantumforge.quickdial.bank.transit.UssdUserSessionRegistry;
import com.quantumforge.quickdial.event.UssdEventPublisher;
import com.quantumforge.quickdial.session.UssdSession;
import com.quantumforge.quickdial.util.GeneralUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleUssdUserSessionRegistry implements UssdUserSessionRegistry {
    private static final ConcurrentMap<String, UssdSession> SESSION_REGISTRY_LOG = new ConcurrentHashMap<>();

    @Override
    public UssdSession getSession(String sessionId, boolean createIfAbsent){
        if(GeneralUtils.isNullOrEmpty(sessionId)){
            throw new IllegalArgumentException("sessionId cannot be null or empty!");
        }
        UssdSession session = SESSION_REGISTRY_LOG.get(sessionId);
        if(Objects.isNull(session) && createIfAbsent){
            session = new UssdSession();
            session.setSessionId(sessionId);
            session.setCreatedAt(LocalDateTime.now());
            session.setFresh(true);
            SESSION_REGISTRY_LOG.put(sessionId, session);
            return session;
        }
        UssdSession finalSession = session;
        GeneralUtils.doIf(session.isFresh(), () -> finalSession.setFresh(false));
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
            UssdEventPublisher.publishSessionPreDestroyedEvent(sessionToBeInvalidated);
            SESSION_REGISTRY_LOG.remove(sessionId);
            UssdEventPublisher.publishSessionPostDestroyedEvent();
        }
    }

    @Override
    public void invalidateSession(UssdSession session){
        if(Objects.isNull(session)){
            throw new IllegalArgumentException("session to be invalidated cannot be null!");
        }
        invalidateSession(session.getSessionId());
    }

    @Override
    public  Collection<UssdSession> getAllSessions(){
        return SESSION_REGISTRY_LOG.values();
    }
}
