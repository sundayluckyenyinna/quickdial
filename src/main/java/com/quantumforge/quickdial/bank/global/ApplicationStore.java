package com.quantumforge.quickdial.bank.global;

public interface ApplicationStore {
    Object getItem(String key);

    <T> T getItem(String key, Class<T> tClass);

    Object getOrDefault(String key, Object defaultValue);

    Object getOrElseThrow(String key, RuntimeException exception);

    void setItem(String key, Object item);

    void setItemIfAbsent(String key, Object item);
}
