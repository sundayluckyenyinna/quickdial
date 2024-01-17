package com.quantumforge.quickdial.execution.result;

import com.quantumforge.quickdial.payload.UssdExecution;
import com.quantumforge.quickdial.session.UssdSession;

public interface UssdExecutionResultResolver {

    Object getResolvedUssdBody(UssdExecution<?> execution, UssdSession session);
    boolean supportsState(UssdExecution.MenuReturnState returnState);
}
