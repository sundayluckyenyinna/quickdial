package com.quantumforge.quickdial.context;

public interface UssdExecutable {

    boolean supportExecutableType(UssdExecutableType executableType);
    UssdExecutableType getExecutableType();

    int mappingLength();
}
