package com.quantumforge.quickdial.execution;

import com.quantumforge.quickdial.bank.transit.UssdUserSessionRegistry;
import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.execution.provider.UssdUserExecutionContextParameterProvider;
import com.quantumforge.quickdial.execution.provider.UssdUserExecutionParameter;
import com.quantumforge.quickdial.payload.QuickDialPayload;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DefaultUssdExecutor implements QuickDialUssdExecutor{

    private final UssdUserSessionRegistry sessionRegistry;
    private final UssdUserExecutionContextParameterProvider contextParameterProvider;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T submit(QuickDialPayload quickDialPayload){
        UssdUserExecutionParameter executionParameter = contextParameterProvider.provideParameter(quickDialPayload);
        UssdSession session = sessionRegistry.getSession(executionParameter.getSessionId());
        UssdUserExecutionContext ussdUserExecutionContext = executionParameter.getFinalUssdUserExecutionContext();
        Object invocationResult = UssdExecutionReflectionInvocationUtils.invokeUssdExecutionForSession(ussdUserExecutionContext, session);
        return (T) invocationResult;
    }
}
