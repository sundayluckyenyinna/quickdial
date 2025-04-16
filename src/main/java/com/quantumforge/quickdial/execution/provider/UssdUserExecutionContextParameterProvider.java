package com.quantumforge.quickdial.execution.provider;

import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.payload.QuickDialPayload;
import com.quantumforge.quickdial.util.GeneralUtils;
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

        String chainedUssdData;
        if(GeneralUtils.isNullOrEmpty(param.getPrefix())) {
            chainedUssdData = QuickDialUtil.staticApplicationChain(param.getMsisdn(), param.getTelco().toUpperCase());
        }else {
            chainedUssdData = QuickDialUtil.staticApplicationChain(param.getMsisdn(), param.getTelco().toUpperCase(), param.getPrefix());
        }

        if(!tokensWithoutBaseCode.isEmpty()) {
            String chainedTokens = QuickDialUtil.staticApplicationChain(tokensWithoutBaseCode);
            chainedUssdData = QuickDialUtil.staticApplicationChain(chainedUssdData, chainedTokens);
        }
        return chainedUssdData;
    }

    default String buildContextDataForContinuedSession(ContextDataBuildParam param){
        String reconstructedOldContextData;
        if(!GeneralUtils.isNullOrEmpty(param.getPrefix())){
            List<String> oldContextDataTokens = QuickDialUtil.tokenizeContextData(param.getOldContextData());
            if(oldContextDataTokens.size() >= 3){
                oldContextDataTokens.set(2, param.getPrefix().trim());
            }else {
                oldContextDataTokens.add(2, param.getPrefix().trim());
            }
            reconstructedOldContextData = QuickDialUtil.staticApplicationChain(oldContextDataTokens);
        }else{
            reconstructedOldContextData = param.getOldContextData();
        }
        if(Objects.nonNull(param.getIncomingInput())){
            return QuickDialUtil.staticApplicationChain(reconstructedOldContextData, param.getIncomingInput());
        }
        return reconstructedOldContextData;
    }

    default String buildFullUssdCodeByInvocationType(String code, String baseCode, String prefix, UssdInvocationType invocationType){
        String fullCode;
        if(invocationType == UssdInvocationType.PROGRESSIVE){
            if(!GeneralUtils.isNullOrEmpty(prefix)) {
                fullCode = code.substring(0, code.lastIndexOf(QuickDialUtil.sProperties.getEndDelimiter()));
                fullCode = fullCode.concat(QuickDialUtil.sProperties.getStartDelimiter() + prefix).concat(QuickDialUtil.sProperties.getEndDelimiter());
            }else{
                fullCode = code;
            }
            fullCode = fullCode.replace(StringValues.BACKWARD_SLASH, StringValues.EMPTY_STRING);
        }
        else if(invocationType == UssdInvocationType.SHORT_CODE){
            String baseCodeWithoutHash = baseCode.substring(0, baseCode.indexOf(QuickDialUtil.sProperties.getEndDelimiter()));
            String replacement;
            if(!GeneralUtils.isNullOrEmpty(prefix)) {
                 replacement = QuickDialUtil.staticApplicationChain(baseCodeWithoutHash, QuickDialUtil.sProperties.getShortCodePrefix(), prefix);
            }else{
                replacement = QuickDialUtil.staticApplicationChain(baseCodeWithoutHash, QuickDialUtil.sProperties.getShortCodePrefix());
            }
            fullCode = code.replace(baseCodeWithoutHash, replacement);
        }else{
            throw new IllegalArgumentException("Invalid invocation type");
        }
        return fullCode;
    }
}
