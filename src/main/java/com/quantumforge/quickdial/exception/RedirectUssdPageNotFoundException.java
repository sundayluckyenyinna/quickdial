package com.quantumforge.quickdial.exception;

public class RedirectUssdPageNotFoundException extends RuntimeException{

    public RedirectUssdPageNotFoundException(){
        super();
    }

    public RedirectUssdPageNotFoundException(String message){
        super(message);
    }
}
