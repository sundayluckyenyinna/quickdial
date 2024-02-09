package com.quantumforge.quickdial.execution;

import com.quantumforge.quickdial.bank.transit.UssdUserSessionRegistry;
import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.execution.provider.UssdUserExecutionContextParameterProvider;
import com.quantumforge.quickdial.execution.provider.UssdUserExecutionParameter;
import com.quantumforge.quickdial.payload.QuickDialPayload;
import com.quantumforge.quickdial.payload.UssdExecution;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DefaultUssdExecutor implements QuickDialUssdExecutor{

    private final UssdUserSessionRegistry sessionRegistry;
    private final UssdUserExecutionContextParameterProvider contextParameterProvider;

    @Override
    public UssdExecution<?> submit(QuickDialPayload quickDialPayload){
        UssdUserExecutionParameter executionParameter = contextParameterProvider.provideParameter(quickDialPayload);
        UssdSession session = sessionRegistry.getSession(executionParameter.getSessionId());
        UssdUserExecutionContext ussdUserExecutionContext = executionParameter.getFinalUssdUserExecutionContext();
        return UssdExecutionReflectionInvocationUtils.invokeUssdExecutionForSession(ussdUserExecutionContext, session);
    }

    @Override
    public <T> UssdExecution<T> submit(QuickDialPayload quickDialPayload, Class<T> tClass){
        UssdExecution<?> execution = submit(quickDialPayload);
        UssdExecution<T> ussdExecution = new UssdExecution<>();
        BeanUtils.copyProperties(execution, ussdExecution);
        ussdExecution.setBody(tClass.cast(execution.getBody()));
        return ussdExecution;
    }
}
