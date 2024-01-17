package com.quantumforge.quickdial.exception;

public class UssdExecutionResolverNotFoundException extends RuntimeException{

    public UssdExecutionResolverNotFoundException(){
        super();
    }

    public UssdExecutionResolverNotFoundException(String message){
        super(message);
    }
}
