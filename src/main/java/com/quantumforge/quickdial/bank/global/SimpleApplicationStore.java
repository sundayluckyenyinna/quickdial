package com.quantumforge.quickdial.bank.global;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleApplicationStore implements ApplicationStore{

    private final ConcurrentMap<Object, Object> store = new ConcurrentHashMap<>();

    @Override
    public Object getItem(Object key){
        if(Objects.isNull(key)){
            return null;
        }
        return store.get(key);
    }

    @Override
    public Object getItem(String key){
        if(Objects.isNull(key)){
            return null;
        }
        return store.get(key);
    }

    @Override
    public <T> T getItem(Object key, Class<T> tClass){
        return Objects.nonNull(getItem(key)) ? tClass.cast(getItem(key)) : null;
    }

    @Override
    public <T> T getItem(String key, Class<T> tClass){
        return Objects.nonNull(getItem(key)) ? tClass.cast(getItem(key)) : null;
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue){
        if(store.containsKey(key)){
            return this.getItem(key);
        }
        return defaultValue;
    }

    @Override
    public Object getOrDefault(String key, Object defaultValue){
        if(store.containsKey(key)){
            return this.getItem(key);
        }
        return defaultValue;
    }

    @Override
    public Object getOrElseThrow(Object key, RuntimeException exception){
        if(store.containsKey(key)){
            return this.getItem(key);
        }
        throw exception;
    }

    @Override
    public void setItem(Object key, Object item){
        if(isQualifiedForInsertion(key, item)){
            store.put(key, item);
        }
    }

    @Override
    public void setItem(String key, Object item){
        if(isQualifiedForInsertion(key, item)){
            store.put(key, item);
        }
    }

    @Override
    public void setItemIfAbsent(Object key, Object item){
        if(isQualifiedForInsertion(key, item)){
            store.putIfAbsent(key, item);
        }
    }

    @Override
    public void removeItem(Object key){
        store.remove(key);
    }

    private static boolean isQualifiedForInsertion(Object key, Object value){
        return Objects.nonNull(key) && Objects.nonNull(value);
    }
}
