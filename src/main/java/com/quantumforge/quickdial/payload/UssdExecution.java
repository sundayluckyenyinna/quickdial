package com.quantumforge.quickdial.payload;

public final class UssdExecution<T> {
    private boolean isRedirect = false;
    private boolean isContinue = false;
    private boolean isEnd = false;
    private T body;

    private UssdExecution(){}

    public static <T> UssdExecution<T> redirect(T body){
        UssdExecution<T> ussdExecution = new UssdExecution<>();
        ussdExecution.isRedirect = true;
        ussdExecution.body = body;
        return ussdExecution;
    }

    public static <T> UssdExecution<T> continues(T body){
        UssdExecution<T> ussdExecution = new UssdExecution<>();
        ussdExecution.isContinue = true;
        ussdExecution.body = body;
        return ussdExecution;
    }

    public static <T> UssdExecution<T> end(T body){
        UssdExecution<T> ussdExecution = new UssdExecution<>();
        ussdExecution.isEnd = true;
        ussdExecution.body = body;
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
}
