package com.quantumforge.quickdial.exception;

public class UnsupportedUssdMessageDocumentSourceException extends RuntimeException{

    public UnsupportedUssdMessageDocumentSourceException(){
        super();
    }

    public UnsupportedUssdMessageDocumentSourceException(String message){
        super(message);
    }
}
