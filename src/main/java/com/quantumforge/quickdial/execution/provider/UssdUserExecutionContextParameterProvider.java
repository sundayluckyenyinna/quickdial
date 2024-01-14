package com.quantumforge.quickdial.execution.provider;

import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.payload.QuickDialPayload;
import com.quantumforge.quickdial.util.QuickDialUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface UssdUserExecutionContextParameterProvider{

    UssdUserExecutionParameter provideParameter(QuickDialPayload quickDialPayload);

    boolean supportsInvocation(UssdInvocationType ussdInvocationType);

    default String buildContextDataForStartSession(ContextDataBuildParam param){
        String baseUssdCodeWithoutHash = param.getBaseCode().replace(QuickDialUtil.sProperties.getEndDelimiter(), StringValues.EMPTY_STRING);
        List<String> tokensWithoutBaseCode = QuickDialUtil.getStaticTokensBetweenDelimiters(param.getOriginatingUssdCode().replace(baseUssdCodeWithoutHash, StringValues.EMPTY_STRING))
                .stream()
                .filter(string -> Objects.nonNull(string) && !string.trim().isEmpty())
                .filter(string -> !string.equalsIgnoreCase(param.getBaseCode()))
                .collect(Collectors.toList());
        String chainedUssdData = QuickDialUtil.staticApplicationChain(param.getMsisdn(), param.getTelco().toUpperCase());

        if(!tokensWithoutBaseCode.isEmpty()) {
            String chainedTokens = QuickDialUtil.staticApplicationChain(tokensWithoutBaseCode);
            chainedUssdData = QuickDialUtil.staticApplicationChain(chainedUssdData, chainedTokens);
        }
        return chainedUssdData;
    }

    default String buildContextDataForContinuedSession(ContextDataBuildParam param){
        if(Objects.nonNull(param.getIncomingInput())){
            return QuickDialUtil.staticApplicationChain(param.getOldContextData(), param.getIncomingInput());
        }
        return param.getOldContextData();
    }

    default String buildFullUssdCodeByInvocationType(String code, String baseCode, UssdInvocationType invocationType){
        String fullCode;
        if(invocationType == UssdInvocationType.PROGRESSIVE){
            fullCode = code;
        }
        else if(invocationType == UssdInvocationType.SHORT_CODE){
            String baseCodeWithoutHash = baseCode.substring(0, baseCode.indexOf(QuickDialUtil.sProperties.getEndDelimiter()));
            String replacement = QuickDialUtil.staticApplicationChain(baseCodeWithoutHash, QuickDialUtil.sProperties.getShortCodePrefix());
            fullCode = code.replace(baseCodeWithoutHash, replacement);
        }else{
            throw new IllegalArgumentException("Invalid invocation type");
        }
        return fullCode;
    }
}
