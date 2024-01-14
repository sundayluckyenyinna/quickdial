package com.quantumforge.quickdial.exception;

public class EmptyUssdUserSessionExecutionContextNavigableStackException extends RuntimeException{

    public EmptyUssdUserSessionExecutionContextNavigableStackException(){
        super("The user navigable execution context stack is empty. User cannot navigate on empty context stack");
    }

    public EmptyUssdUserSessionExecutionContextNavigableStackException(String sessionId){
        super(String.format("The user with sessionId: %s navigable execution context stack is empty. User cannot navigate on empty context stack", sessionId));
    }
}
