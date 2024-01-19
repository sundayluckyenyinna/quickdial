package com.quantumforge.quickdial.execution.result;

import com.quantumforge.quickdial.bank.transit.UssdUserSessionRegistry;
import com.quantumforge.quickdial.payload.UssdExecution;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class EndUssdExecutionResultResolver implements UssdExecutionResultResolver{

    private final UssdUserSessionRegistry sessionRegistry;

    @Override
    public Object getResolvedUssdBody(UssdExecution<?> execution, UssdSession session) {
        sessionRegistry.invalidateSession(session);
        return execution.getBody();
    }

    @Override
    public boolean supportsState(UssdExecution.MenuReturnState returnState) {
        return returnState == UssdExecution.MenuReturnState.END;
    }
}
