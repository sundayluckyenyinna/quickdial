package com.quantumforge.quickdial.exception;

public class UssdMessageNotFoundException extends RuntimeException{

    public UssdMessageNotFoundException(){
        super();
    }

    public UssdMessageNotFoundException(String message){
        super(message);
    }
}
