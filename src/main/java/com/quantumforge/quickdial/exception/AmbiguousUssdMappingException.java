package com.quantumforge.quickdial.exception;

public class AmbiguousUssdMappingException extends RuntimeException {


    public AmbiguousUssdMappingException(){
        super("Ambiguous ussd mapping");
    }

    public AmbiguousUssdMappingException(String message){
       super(message);
    }
}
