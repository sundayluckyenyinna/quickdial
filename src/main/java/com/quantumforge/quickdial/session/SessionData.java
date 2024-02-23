package com.quantumforge.quickdial.session;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class SessionData {

    private final UssdSession session;
    public SessionData(UssdSession session){
        this.session = session;
    }

    public UssdSession getOwnSession(){
        return this.session;
    }

    private final ConcurrentMap<String, Object> SESSION_DATA_REPO = new ConcurrentHashMap<>();

    public SessionData keepAttribute(String key, Object attr){
        addOrThrowException(key, attr);
        return this;
    }

    public Object getAttribute(String key){
        return SESSION_DATA_REPO.get(key);
    }

    public <T> T getAttribute(String key, Class<T> tClass){
        Object attribute = SESSION_DATA_REPO.get(key);
        return Objects.nonNull(attribute) ? getCast(attribute, tClass) : null;
    }

    public void removeAttribute(String key){
        SESSION_DATA_REPO.remove(key);
    }

    public Object removeAndGetAttr(String key){
        return SESSION_DATA_REPO.remove(key);
    }

    public <T> T removeAndGetAttrOfType(String key, Class<T> tClass){
        Object removed = removeAndGetAttr(key);
        return getCast(removed, tClass);
    }

    public Object  replaceAttr(String key, Object attr){
        return SESSION_DATA_REPO.replace(key, attr);
    }

    public Map<String, Object> getSessionRepo(){
        return SESSION_DATA_REPO;
    }

    private <T> T getCast(Object attribute, Class<T> tClass){
        if(tClass.isInstance(attribute)){
            return tClass.cast(attribute);
        }
        throw new ClassCastException(String.format("Session attribute of type: %s cannot be assignable from supplied type: %s", attribute.getClass(), tClass));
    }

    private void addOrThrowException(String key, Object attr){
        if(Objects.nonNull(key) && Objects.nonNull(attr)){
            SESSION_DATA_REPO.put(key, attr);
            return;
        }
        throw new IllegalArgumentException("Neither key nor session attribute can be null");
    }
}
