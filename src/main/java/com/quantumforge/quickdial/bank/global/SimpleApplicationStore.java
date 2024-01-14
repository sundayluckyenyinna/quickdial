package com.quantumforge.quickdial.bank.global;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This class encapsulates the complete store for the application in a global manner.
 * This class implements the ApplicationStore in a default way.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleApplicationStore implements ApplicationStore{

    private final ConcurrentMap<String, Object> store = new ConcurrentHashMap<>();

    @Override
    public Object getItem(String key){
        if(Objects.isNull(key)){
            return null;
        }
        return store.get(key);
    }

    @Override
    public <T> T getItem(String key, Class<T> tClass){
        return tClass.cast(getItem(key));
    }

    @Override
    public Object getOrDefault(String key, Object defaultValue){
        if(store.containsKey(key)){
            return this.getItem(key);
        }
        return defaultValue;
    }

    @Override
    public Object getOrElseThrow(String key, RuntimeException exception){
        if(store.containsKey(key)){
            return this.getItem(key);
        }
        throw exception;
    }

    @Override
    public void setItem(String key, Object item){
        if(isQualifiedForInsertion(key, item)){
            store.put(key, item);
        }
    }

    @Override
    public void setItemIfAbsent(String key, Object item){
        if(isQualifiedForInsertion(key, item)){
            store.putIfAbsent(key, item);
        }
    }

    private static boolean isQualifiedForInsertion(String key, Object value){
        return Objects.nonNull(key) && Objects.nonNull(value);
    }
}
