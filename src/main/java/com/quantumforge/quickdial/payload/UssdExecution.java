package com.quantumforge.quickdial.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public final class UssdExecution<T> {
    private boolean isRedirect = false;
    private boolean isContinue = false;
    private boolean isEnd = false;
    @Getter
    @Setter
    private T body;

    @Getter
    @Setter
    private MenuReturnState returnState = MenuReturnState.CONTINUE;

    @Getter
    @Setter
    private String redirectUssdPageId;

    @Getter
    @Setter
    private String redirectUssdPageInput;

    @Getter
    @Setter
    private Class<?> currentCallableClass;

    public UssdExecution(){}

    public static <T> UssdExecution<T> redirect(String ussdPageId){
        UssdExecution<T> ussdExecution = new UssdExecution<>();
        ussdExecution.isRedirect = true;
        ussdExecution.body = null;
        ussdExecution.returnState = MenuReturnState.REDIRECT;
        ussdExecution.redirectUssdPageId = ussdPageId;
        ussdExecution.redirectUssdPageInput = null;
        return ussdExecution;
    }

    public static <T> UssdExecution<T> redirect(String ussdPageId, String input){
        UssdExecution<T> ussdExecution = new UssdExecution<>();
        ussdExecution.isRedirect = true;
        ussdExecution.body = null;
        ussdExecution.returnState = MenuReturnState.REDIRECT;
        ussdExecution.redirectUssdPageId = ussdPageId;
        ussdExecution.redirectUssdPageInput = input;
        return ussdExecution;
    }

    public static <T> UssdExecution<T> continues(T body){
        UssdExecution<T> ussdExecution = new UssdExecution<>();
        ussdExecution.isContinue = true;
        ussdExecution.body = body;
        ussdExecution.returnState = MenuReturnState.CONTINUE;
        return ussdExecution;
    }

    public static <T> UssdExecution<T> end(T body){
        UssdExecution<T> ussdExecution = new UssdExecution<>();
        ussdExecution.isEnd = true;
        ussdExecution.body = body;
        ussdExecution.returnState = MenuReturnState.END;
        return ussdExecution;
    }

    public boolean isRedirect(){
        return this.isRedirect;
    }

    public boolean isContinue(){
        return this.isContinue;
    }

    public boolean isEnd(){
        return this.isEnd;
    }

    public enum MenuReturnState{
        CONTINUE,
        REDIRECT,
        END
    }
}
