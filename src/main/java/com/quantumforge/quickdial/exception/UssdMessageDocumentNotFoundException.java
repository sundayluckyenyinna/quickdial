package com.quantumforge.quickdial.exception;

public class UssdMessageDocumentNotFoundException extends RuntimeException{

    public UssdMessageDocumentNotFoundException(){
        super();
    }

    public UssdMessageDocumentNotFoundException(String message){
        super(message);
    }
}
