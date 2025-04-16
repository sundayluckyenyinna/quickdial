package com.quantumforge.quickdial.util;

import com.quantumforge.quickdial.bank.transit.UssdMapType;
import com.quantumforge.quickdial.bootstrap.CommonUssdConfigProperties;
import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.execution.provider.UssdInvocationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = { UssdUtilProperties.class, CommonUssdConfigProperties.class })
public class QuickDialUtil {

    private final UssdUtilProperties properties;
    private final CommonUssdConfigProperties configProperties;
    public static UssdUtilProperties sProperties;
    public static CommonUssdConfigProperties sConfigProperties;

    public UssdMapType getUssdMapType(String mapping){
        if(isDirectMapping(mapping)){
            return UssdMapType.DIRECT_MAPPING;
        }
        else if(isParamMapping(mapping)){
            return UssdMapType.PARAM_MAPPING;
        }
        else if(isShortCodeMapping(mapping)){
            return UssdMapType.SHORT_CODE_MAPPING;
        }
        else{
            return UssdMapType.UNKNOWN;
        }
    }

    public boolean isDirectMapping(String mapping){
        return !isParamMapping(mapping);
    }

    public boolean isParamMapping(String mapping){
        Pattern pattern = Pattern.compile(StringValues.PARAMETERIZED_PATTERN);
        Matcher matcher = pattern.matcher(mapping);
        return matcher.find();
    }

    public boolean isShortCodeMapping(String mapping){
        List<String> tokens = getBetweenTokensInList(mapping, properties.getStartDelimiter(), properties.getEndDelimiter());
        if(!tokens.isEmpty() && tokens.size() >= 2){
            String secondElement = tokens.get(1);
            return Objects.nonNull(secondElement) && secondElement.toUpperCase().equalsIgnoreCase(properties.getShortCodePrefix());
        }
        return false;
    }

    public List<String> getTokensBetweenDelimiters(String string){
        return getBetweenTokensInList(string, properties.getStartDelimiter(), properties.getEndDelimiter());
    }

    public static List<String> getStaticTokensBetweenDelimiters(String string){
        return getBetweenTokensInList(string, sProperties.getStartDelimiter(), sProperties.getEndDelimiter());
    }

    public String applicationChain(String ... strings){
        return String.join(properties.getStartDelimiter(), strings).replace(StringValues.BACKWARD_SLASH, StringValues.EMPTY_STRING);
    }

    public static String staticApplicationChain(String ... strings){
        return String.join(sProperties.getStartDelimiter(), strings).replace(StringValues.BACKWARD_SLASH, StringValues.EMPTY_STRING);
    }

    public static String staticApplicationChain(Collection<String> strings){
        return String.join(sProperties.getStartDelimiter(), strings).replace(StringValues.BACKWARD_SLASH, StringValues.EMPTY_STRING);
    }

    public static boolean isParamPlaceHolder(String string){
        return Objects.nonNull(string) && string.trim().startsWith(StringValues.OPENING_BRACE) && string.trim().endsWith(StringValues.CLOSING_BRACE);
    }

    public static List<String> getBetweenTokensInList(String string, String delimiter1, String delimiter2){
        List<String> tokensWithLastDelimiterIncluded = tokenizeToList(string, delimiter1);
        int size = tokensWithLastDelimiterIncluded.size();
        if(tokensWithLastDelimiterIncluded.isEmpty()){
            return tokensWithLastDelimiterIncluded;
        }
        String lastElement = tokensWithLastDelimiterIncluded.get(size - 1);
        lastElement = lastElement.replace(delimiter2, StringValues.EMPTY_STRING)
                .replace(StringValues.BACKWARD_SLASH, StringValues.EMPTY_STRING);
        tokensWithLastDelimiterIncluded.set(size - 1, lastElement);
        return tokensWithLastDelimiterIncluded;
    }

    public static List<String> tokenizeToList(String string, String delimiter){
        return Arrays.stream(string.trim().split(delimiter))
                .filter(s -> !s.trim().isEmpty())
                .collect(Collectors.toList());
    }

    public static String cleanFullCodeOffBaseCodeAndShortCodePrefix(String fullCode){
        String baseCodeWithoutHash = sConfigProperties.getBaseUssdCode().substring(0, sConfigProperties.getBaseUssdCode().lastIndexOf(sProperties.getEndDelimiter())).trim();
        String cleanedCode = fullCode.replace(baseCodeWithoutHash, StringValues.EMPTY_STRING).replace(sProperties.getShortCodePrefix(), StringValues.EMPTY_STRING);
        return GeneralUtils.replaceConsecutiveTokens(cleanedCode, sProperties.getStartDelimiter().replace(StringValues.BACKWARD_SLASH, StringValues.EMPTY_STRING)).trim();
    }

    public static String cleanContextDataOffMsisdnAndTelco(String contextData, String msisdn, String telco){
        String concatenatedPatch = staticApplicationChain(msisdn, telco);
        String offset = contextData.replace(concatenatedPatch, StringValues.EMPTY_STRING);
        if(!offset.trim().isEmpty()){
            return offset.substring(offset.indexOf(sProperties.getStartDelimiter().replace(StringValues.BACKWARD_SLASH, StringValues.EMPTY_STRING)) + 1).trim();
        }
        return offset.trim();
    }

    public String extendUssdCode(String incomingCode, UssdInvocationType invocationType, String prefix, String input){
        String fullCode;
        String incomingCodeWithoutHash = incomingCode.replace(properties.getEndDelimiter(), StringValues.EMPTY_STRING).trim();
        if(!GeneralUtils.isNullOrEmpty(prefix) && !incomingCode.contains(prefix)) {
            fullCode = incomingCodeWithoutHash
                    .concat(invocationType == UssdInvocationType.SHORT_CODE ? sProperties.getStartDelimiter() + sProperties.getShortCodePrefix() : StringValues.EMPTY_STRING)
                    .concat(properties.getStartDelimiter())
                    .concat(prefix)
                    .concat(properties.getStartDelimiter())
                    .concat(input)
                    .concat(properties.getEndDelimiter())
                    .replace(StringValues.BACKWARD_SLASH, StringValues.EMPTY_STRING);
        }else{
            fullCode = incomingCodeWithoutHash.concat(properties.getStartDelimiter()).concat(input).concat(properties.getEndDelimiter())
                    .replace(StringValues.BACKWARD_SLASH, StringValues.EMPTY_STRING);
        }
        return fullCode;
    }

    public static List<String> tokenizeContextData(String contextData){
        String[] tokenArray = contextData.split(sProperties.getStartDelimiter());
        return new ArrayList<>(List.of(tokenArray));
    }

    public static String buildWithSimulationPrefixAndInputs(String prefix, String ... inputs){
        String baseCodeWithoutHash = sConfigProperties.getBaseUssdCodeWithoutEndDelimiter();
        if(!GeneralUtils.isNullOrEmpty(prefix)){
            baseCodeWithoutHash = baseCodeWithoutHash.concat(sProperties.getStartDelimiter()).concat(prefix);
        }
        String chainedInputs = QuickDialUtil.staticApplicationChain(inputs);
        return baseCodeWithoutHash
                .concat(sProperties.getStartDelimiter())
                .concat(chainedInputs)
                .concat(sProperties.getEndDelimiter())
                .replace(StringValues.BACKWARD_SLASH, StringValues.EMPTY_STRING);
    }

    @Bean
    public String configureStaticUssdUtilProperties(){
        sProperties = properties;
        sConfigProperties = configProperties;
        return StringValues.BEAN_CREATION_SUCCESS;
    }
}
