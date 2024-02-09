package com.quantumforge.quickdial.execution.result;

import com.quantumforge.quickdial.payload.UssdExecution;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ContinueUssdExecutionResultResolver implements UssdExecutionResultResolver{

    @Override
    public UssdExecution<?> getResolvedUssdBody(UssdExecution<?> execution, UssdSession session) {
        return execution;
    }

    @Override
    public boolean supportsState(UssdExecution.MenuReturnState returnState) {
        return returnState == UssdExecution.MenuReturnState.CONTINUE;
    }
}
