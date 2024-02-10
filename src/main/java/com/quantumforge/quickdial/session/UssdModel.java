package com.quantumforge.quickdial.session;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class UssdModel {

    private final UssdSession session;
    public UssdModel(UssdSession session){
        this.session = session;
    }

    public UssdSession getOwnSession(){
        return this.session;
    }

    private final Map<String, Object> USSD_MODEL_REPO = new ConcurrentHashMap<>();

    public UssdModel addObject(String key, Object attr){
        addOrThrowException(key, attr);
        return this;
    }

    public Object getObject(String key){
        return USSD_MODEL_REPO.get(key);
    }

    public <T> T getObject(String key, Class<T> tClass){
        Object attribute = USSD_MODEL_REPO.get(key);
        return Objects.nonNull(attribute) ? getCast(attribute, tClass) : null;
    }

    public void removeObject(String key){
        USSD_MODEL_REPO.remove(key);
    }

    public Object removeAndGetObject(String key){
        return USSD_MODEL_REPO.remove(key);
    }

    public <T> T removeAndGetObject(String key, Class<T> tClass){
        Object removed = removeAndGetObject(key);
        return getCast(removed, tClass);
    }

    public Object  replaceObject(String key, Object attr){
        return USSD_MODEL_REPO.replace(key, attr);
    }


    public Map<String, Object> getModelMap(){
        return USSD_MODEL_REPO;
    }

    private <T> T getCast(Object attribute, Class<T> tClass){
        if(tClass.isInstance(attribute)){
            return tClass.cast(attribute);
        }
        throw new ClassCastException(String.format("UssdModel data of type: %s cannot be assignable from supplied type: %s", attribute.getClass(), tClass));
    }

    private void addOrThrowException(String key, Object attr){
        if(Objects.nonNull(key) && Objects.nonNull(attr)){
            USSD_MODEL_REPO.put(key, attr);
            return;
        }
        throw new IllegalArgumentException("Neither key nor session attribute can be null");
    }
}
