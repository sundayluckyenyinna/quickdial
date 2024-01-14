package com.quantumforge.quickdial.exception;

public class UnknownUssdMapTypeException extends RuntimeException{

    public UnknownUssdMapTypeException(){
        super("Unknown Ussd mapping type");
    }

    public UnknownUssdMapTypeException(String message){
        super(message);
    }
}
