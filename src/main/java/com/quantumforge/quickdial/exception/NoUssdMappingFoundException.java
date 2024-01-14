package com.quantumforge.quickdial.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoUssdMappingFoundException extends RuntimeException{

    private String ussdCode;
    public NoUssdMappingFoundException(){
        super("No mapping found");
    }

    public NoUssdMappingFoundException(String code){
        super(String.format("No mapping handler found for ussd mapping: %s", code));
        this.ussdCode = code;
    }
}
