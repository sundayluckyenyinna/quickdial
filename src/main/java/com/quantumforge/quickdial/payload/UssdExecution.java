package com.quantumforge.quickdial.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public final class UssdExecution<T> {
    private boolean shouldRedirect = false;
    private boolean shouldContinue = false;
    private boolean shouldEnd = false;
    @Setter
    private T body;

    @Setter
    private MenuReturnState returnState = MenuReturnState.CONTINUE;

    @Setter
    private String redirectUssdPageId;

    @Setter
    private String redirectUssdPageInput;

    @Setter
    private Class<?> currentCallableClass;

    public UssdExecution(){}

    public static <T> UssdExecution<T> redirect(String ussdPageId){
        UssdExecution<T> ussdExecution = new UssdExecution<>();
        ussdExecution.shouldRedirect = true;
        ussdExecution.body = null;
        ussdExecution.returnState = MenuReturnState.REDIRECT;
        ussdExecution.redirectUssdPageId = ussdPageId;
        ussdExecution.redirectUssdPageInput = null;
        return ussdExecution;
    }

    public static <T> UssdExecution<T> redirect(String ussdPageId, String input){
        UssdExecution<T> ussdExecution = new UssdExecution<>();
        ussdExecution.shouldRedirect = true;
        ussdExecution.body = null;
        ussdExecution.returnState = MenuReturnState.REDIRECT;
        ussdExecution.redirectUssdPageId = ussdPageId;
        ussdExecution.redirectUssdPageInput = input;
        return ussdExecution;
    }

    public static <T> UssdExecution<T> continues(T body){
        UssdExecution<T> ussdExecution = new UssdExecution<>();
        ussdExecution.shouldContinue = true;
        ussdExecution.body = body;
        ussdExecution.returnState = MenuReturnState.CONTINUE;
        return ussdExecution;
    }

    public static <T> UssdExecution<T> end(T body){
        UssdExecution<T> ussdExecution = new UssdExecution<>();
        ussdExecution.shouldEnd = true;
        ussdExecution.body = body;
        ussdExecution.returnState = MenuReturnState.END;
        return ussdExecution;
    }

    public enum MenuReturnState{
        CONTINUE,
        REDIRECT,
        END
    }
}
