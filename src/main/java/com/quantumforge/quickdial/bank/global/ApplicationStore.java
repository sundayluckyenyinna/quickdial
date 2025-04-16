package com.quantumforge.quickdial.bank.global;

public interface ApplicationStore {
    Object getItem(Object key);

    Object getItem(String key);

    <T> T getItem(Object key, Class<T> tClass);

    <T> T getItem(String key, Class<T> tClass);

    Object getOrDefault(Object key, Object defaultValue);

    Object getOrDefault(String key, Object defaultValue);

    Object getOrElseThrow(Object key, RuntimeException exception);

    void setItem(Object key, Object item);

    void setItem(String key, Object item);

    void setItemIfAbsent(Object key, Object item);

    void removeItem(Object key);
}
