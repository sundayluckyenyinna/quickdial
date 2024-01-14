package com.quantumforge.quickdial.exception;

public class InvalidUssdMessageSourceException extends RuntimeException{
    public InvalidUssdMessageSourceException(){
        super();
    }

    public InvalidUssdMessageSourceException(String message){
        super(message);
    }
}
