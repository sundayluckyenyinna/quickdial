package com.quantumforge.quickdial.util;

public interface InputOperation<T> extends Operation{

    void acceptAndApply(T input);
}
