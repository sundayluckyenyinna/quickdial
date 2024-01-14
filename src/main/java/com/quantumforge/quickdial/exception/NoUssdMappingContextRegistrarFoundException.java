package com.quantumforge.quickdial.exception;

public class NoUssdMappingContextRegistrarFoundException extends RuntimeException{

    public NoUssdMappingContextRegistrarFoundException(){
        super("No suitable registrar found to buildDocument the given ussd mapping to the ussd mapping registry");
    }

    public NoUssdMappingContextRegistrarFoundException(String mapping){
        super(String.format("No suitable registrar found to buildDocument the given ussd mapping: %s to the ussd mapping registry", mapping));
    }
}
