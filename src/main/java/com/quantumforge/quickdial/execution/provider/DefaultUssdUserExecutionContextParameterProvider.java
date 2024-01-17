package com.quantumforge.quickdial.execution.provider;

import com.quantumforge.quickdial.bank.transit.UssdMappingRegistry;
import com.quantumforge.quickdial.bootstrap.CommonUssdConfigProperties;
import com.quantumforge.quickdial.context.UssdExecutionContext;
import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.interceptor.*;
import com.quantumforge.quickdial.payload.QuickDialPayload;
import com.quantumforge.quickdial.session.UssdSession;
import com.quantumforge.quickdial.util.QuickDialUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DefaultUssdUserExecutionContextParameterProvider implements UssdUserExecutionContextParameterProvider{

    private final QuickDialUtil quickDialUtil;
    private final UssdMappingRegistry ussdMappingRegistry;
    private final CommonUssdConfigProperties ussdConfigProperties;
    private final UssdRegistrationInterceptor registrationInterceptor;
    private final UssdInputInterceptorExecution inputInterceptorExecution;

    @Override
    public UssdUserExecutionParameter provideParameter(QuickDialPayload payload) {
        UssdUserExecutionContext finalExecutableContext;
        UssdSession session = registrationInterceptor.registerSession(payload.getSessionId());
        if(payload.isSessionStarting()){
            finalExecutableContext = getUssdUserExecutionContextForStartSession(payload);
        } else {
            finalExecutableContext = getUssdUserExecutionContextForContinuedSession(payload);
        }
        UssdUserExecutionParameter parameter = new UssdUserExecutionParameter();
        parameter.setFinalUssdUserExecutionContext(finalExecutableContext);
        parameter.setUssdSession(session);
        parameter.setSessionId(session.getSessionId());
        return parameter;
    }

    @Override
    public boolean supportsInvocation(UssdInvocationType ussdInvocationType){
        return ussdInvocationType == UssdInvocationType.PROGRESSIVE || ussdInvocationType == UssdInvocationType.SHORT_CODE;
    }

    private UssdUserExecutionContext getUssdUserExecutionContextForStartSession(QuickDialPayload payload){
        String fullCode, contextData;
        UssdUserExecutionContext finalExecutableContext;
        UssdSession session = registrationInterceptor.registerSession(payload.getSessionId());

        fullCode = buildFullUssdCodeByInvocationType(payload.getOriginatingCode(), ussdConfigProperties.getBaseUssdCode(), payload.getInvocationType());
        contextData = buildContextDataForStartSession(getContextDataBuildParameter(payload, null));
        UssdExecutionContext context = ussdMappingRegistry.getMatchingUssdExecutionContextForMapping(fullCode);

        UssdUserExecutionContext incomingContext = new UssdUserExecutionContext();
        incomingContext.setUssdCode(fullCode);
        incomingContext.setContextData(contextData);
        buildUserUssdExecutionContext(payload, fullCode, context, incomingContext);

        finalExecutableContext = incomingContext;
        session.updateUserUssdNavigationContext(finalExecutableContext);
        return finalExecutableContext;
    }

    private UssdUserExecutionContext getUssdUserExecutionContextForContinuedSession(QuickDialPayload payload){
        String fullCode;
        UssdUserExecutionContext finalExecutableContext = null;
        UssdSession session = registrationInterceptor.registerSession(payload.getSessionId());

        // Intercept and check for the validity of the input
        UssdUserExecutionContextInterceptionResult inputValidationInterception = inputInterceptorExecution.checkValidInputInterception(payload.getInput(), session);
        if(inputValidationInterception.isIntercepted()){
            finalExecutableContext = inputValidationInterception.getResultingContext();
            return finalExecutableContext;
        }

        UssdUserExecutionContext currentContext = session.getExecutionContextChain().getCurrentElement();
        if(isSpecialInput(payload.getInput())){
            fullCode = currentContext.getUssdCode();
        }else {
            fullCode = quickDialUtil.extendUssdCode(currentContext.getUssdCode(), payload.getInput());
        }

        String newContextData = buildContextDataForContinuedSession(getContextDataBuildParameter(payload, currentContext.getContextData()));
        UssdExecutionContext newContext = ussdMappingRegistry.getMatchingUssdExecutionContextForMapping(fullCode);
        UssdUserExecutionContext incomingContext = new UssdUserExecutionContext();
        incomingContext.setContextData(newContextData);
        incomingContext.setInput(payload.getInput());
        buildUserUssdExecutionContext(payload, fullCode, newContext, incomingContext);

        UssdUserExecutionContextInterceptionResult interceptionResult = inputInterceptorExecution.checkSpecialInterception(incomingContext, session);
        if(interceptionResult.isIntercepted()){
            finalExecutableContext = interceptionResult.getResultingContext();
        }

        if(Objects.isNull(finalExecutableContext)){
            finalExecutableContext = incomingContext;
            session.updateUserUssdNavigationContext(finalExecutableContext);
        }
        return finalExecutableContext;
    }

    private void buildUserUssdExecutionContext(QuickDialPayload payload, String fullCode, UssdExecutionContext newContext, UssdUserExecutionContext incomingContext) {
        incomingContext.setExecutionContext(newContext);
        incomingContext.setUssdCode(fullCode);
        incomingContext.setMsisdn(payload.getMsisdn());
        incomingContext.setTelco(payload.getTelco());
        incomingContext.setInvocationType(payload.getInvocationType());
        incomingContext.setStartingSession(payload.isSessionStarting());
        incomingContext.setShortCodeContext(payload.isShortCodeString());
        incomingContext.setPrefix(payload.getPrefix());
    }

    private ContextDataBuildParam getContextDataBuildParameter(QuickDialPayload payload, String oldContextData){
        return ContextDataBuildParam.builder()
                .msisdn(payload.getMsisdn())
                .telco(payload.getTelco())
                .baseCode(ussdConfigProperties.getBaseUssdCode())
                .originatingUssdCode(payload.getOriginatingCode())
                .incomingInput(payload.getInput())
                .oldContextData(oldContextData)
                .build();
    }

    private boolean isSpecialInput(String input){
        return Arrays.asList(ussdConfigProperties.getGoBackOption(), ussdConfigProperties.getGoForwardOption()).contains(input);
    }
}
