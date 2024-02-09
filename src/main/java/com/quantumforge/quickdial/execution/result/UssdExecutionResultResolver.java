package com.quantumforge.quickdial.execution.result;

import com.quantumforge.quickdial.payload.UssdExecution;
import com.quantumforge.quickdial.session.UssdSession;

public interface UssdExecutionResultResolver {

    UssdExecution<?> getResolvedUssdBody(UssdExecution<?> execution, UssdSession session);
    boolean supportsState(UssdExecution.MenuReturnState returnState);
}
