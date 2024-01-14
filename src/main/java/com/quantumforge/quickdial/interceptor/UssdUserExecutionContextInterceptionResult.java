package com.quantumforge.quickdial.interceptor;

import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UssdUserExecutionContextInterceptionResult {

    private UssdUserExecutionContext resultingContext = null;
    private boolean isIntercepted = false;

    public static UssdUserExecutionContextInterceptionResult getNoInterceptionInstance(){
        UssdUserExecutionContextInterceptionResult result = new UssdUserExecutionContextInterceptionResult();
        result.setIntercepted(false);
        result.setResultingContext(null);
        return result;
    }
}
