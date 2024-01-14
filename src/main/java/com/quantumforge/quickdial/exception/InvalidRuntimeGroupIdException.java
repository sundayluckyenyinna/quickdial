package com.quantumforge.quickdial.exception;

public class InvalidRuntimeGroupIdException extends RuntimeException{

    public InvalidRuntimeGroupIdException(){
        super("Invalid runtime groupId");
    }

    public InvalidRuntimeGroupIdException(String groupId){
        super(String.format("Invalid runtime groupId: %s", groupId));
    }
}
