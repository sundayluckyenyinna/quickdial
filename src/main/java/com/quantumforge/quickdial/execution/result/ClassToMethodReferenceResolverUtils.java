package com.quantumforge.quickdial.execution.result;

import com.quantumforge.quickdial.bootstrap.CommonUssdConfigProperties;
import com.quantumforge.quickdial.context.UssdExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = { CommonUssdConfigProperties.class, UssdRedirectConfigProperties.class})
public class ClassToMethodReferenceResolverUtils {

    private final static String SAME_CLASS_REFERENCE = "this";
    private final UssdRedirectConfigProperties redirectConfigProperties;

    public String resolveUssdContextId(UssdExecutionContext ussdExecutionContext){
        String resultantId = ussdExecutionContext.getUssdSubMenuMapping().id();
        if(Objects.nonNull(resultantId) && !resultantId.trim().isEmpty()){
            return resultantId;
        }
        String simpleClassName = ussdExecutionContext.getCallableClass().getSimpleName();
        String simpleMethodName = ussdExecutionContext.getInvocableMethod().getName();
        String joiner = getReferenceCoordinateJoinerByReferenceFormat();
        return String.join(joiner, simpleClassName, simpleMethodName);
    }

    public String resolveUssdContextIdFromRedirectionRule(String redirectionId, Class<?> callableClass){
         if(redirectionId.trim().startsWith(SAME_CLASS_REFERENCE)){
             String simpleClassName = callableClass.getSimpleName();
             return redirectionId.replace(SAME_CLASS_REFERENCE, simpleClassName);
         }
         return redirectionId;
    }

    private String getReferenceCoordinateJoinerByReferenceFormat(){
        try {
            return RedirectReferenceFormat.valueOf(redirectConfigProperties.getReferenceFormat().toUpperCase()).getJoiner();
        }catch (Exception ignored){
            return RedirectReferenceFormat.DEFAULT_REFERENCE.getJoiner();
        }
    }
}
