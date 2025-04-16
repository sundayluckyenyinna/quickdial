package com.quantumforge.quickdial.execution;

import com.quantumforge.quickdial.annotation.SessionValue;
import com.quantumforge.quickdial.annotation.UssdParam;
import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.context.UserUssdContext;
import com.quantumforge.quickdial.context.UssdExecutionContext;
import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.exception.UssdExecutionResolverNotFoundException;
import com.quantumforge.quickdial.execution.result.UssdExecutionResultResolver;
import com.quantumforge.quickdial.payload.UssdExecution;
import com.quantumforge.quickdial.session.SessionData;
import com.quantumforge.quickdial.session.UssdModel;
import com.quantumforge.quickdial.session.UssdSession;
import com.quantumforge.quickdial.util.GeneralUtils;
import com.quantumforge.quickdial.util.QuickDialUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UssdExecutionReflectionInvocationUtils {

    @Qualifier("conversionService")
    private final ConversionService beanConversionService;
    private final List<UssdExecutionResultResolver> beanExecutionResultResolvers;
    private static ConversionService conversionService;
    private static List<UssdExecutionResultResolver> executionResultResolvers;
    @Bean
    public String configureConversionService(){
        conversionService = beanConversionService;
        executionResultResolvers = beanExecutionResultResolvers;
        return StringValues.BEAN_CREATION_SUCCESS;
    }

    public static UssdExecution<?> invokeUssdExecutionForSession(UssdUserExecutionContext ussdUserExecutionContext, UssdSession session){
        String ussdMapping = ussdUserExecutionContext.getExecutionContext().getUssdMapping(); // this might contain registered mappings
        String contextData = ussdUserExecutionContext.getContextData();  // this contains raw data
        String msisdn = ussdUserExecutionContext.getMsisdn();
        String telco = ussdUserExecutionContext.getTelco();
        UssdExecutionContext ussdExecutionContext = ussdUserExecutionContext.getExecutionContext();
        Method invocableMethod = ussdExecutionContext.getInvocableMethod();
        Object invocableObject = ussdExecutionContext.getCallableClassObject();

        // Remove the base code from the fullCode and the mobile number and telco from the context-data
        String mappingWithoutBaseCode = QuickDialUtil.cleanFullCodeOffBaseCodeAndShortCodePrefix(ussdMapping);
        String cleanedContextData = QuickDialUtil.cleanContextDataOffMsisdnAndTelco(contextData, msisdn, telco);

        // Build the exportable parameters
        UserUssdContext userUssdContext = buildUserUssdContext(ussdUserExecutionContext);
        ExportableUssdMethodParameter exportableUssdMethodParameter = ExportableUssdMethodParameter.builder()
                .sessionData(session.getSessionData())
                .userUssdContext(userUssdContext)
                .ussdModel(session.getUssdModel())
                .session(session)
                .build();

        Map<String, String> parameterValueMap = getUssdParamValueMapOnContextData(mappingWithoutBaseCode, cleanedContextData);
        Object[] invocableMethodParameters = getConvertedMethodParameterArgument(parameterValueMap, invocableMethod, exportableUssdMethodParameter);

        Object rawMethodInvocation = ReflectionUtils.invokeMethod(invocableMethod, invocableObject, invocableMethodParameters);
        UssdExecution<?> executionResult = getUssdExecutionResultFromRawInvocation(rawMethodInvocation);
        executionResult.setCurrentCallableClass(invocableObject.getClass());
        UssdExecutionResultResolver resultResolver = getSupportingResultResolver(executionResult.getReturnState());
        return resultResolver.getResolvedUssdBody(executionResult, session);
    }

    private static UssdExecutionResultResolver getSupportingResultResolver(UssdExecution.MenuReturnState menuReturnState){
        return executionResultResolvers
                 .stream().filter(ussdExecutionResultResolver -> ussdExecutionResultResolver.supportsState(menuReturnState))
                 .findFirst()
                 .orElseThrow(() -> new UssdExecutionResolverNotFoundException("Could not find Ussd execution result resolver"));
    }

    private static UssdExecution<?> getUssdExecutionResultFromRawInvocation(Object rawMethodInvocation){
        UssdExecution<?> executionResult;
        if(rawMethodInvocation instanceof UssdExecution){
            executionResult = (UssdExecution<?>) rawMethodInvocation;
        }
        else{
            executionResult = UssdExecution.continues(rawMethodInvocation);
        }
        return executionResult;
    }

    private static Map<String, String> getUssdParamValueMapOnContextData(String mappingWithoutUssdCode, String cleanedContextData){
        List<String> tokenizedUssdMappingWithoutBaseCode = QuickDialUtil.getStaticTokensBetweenDelimiters(mappingWithoutUssdCode);
        List<String> tokenizeCleanedContextData = QuickDialUtil.getStaticTokensBetweenDelimiters(cleanedContextData);
        Map<String, String> result = new LinkedHashMap<>();
        for(int i = 0; i < tokenizedUssdMappingWithoutBaseCode.size(); i++){
            String token = tokenizedUssdMappingWithoutBaseCode.get(i);
            if(QuickDialUtil.isParamPlaceHolder(token)){
                String correspondingContextDataValue;
                try{
                    correspondingContextDataValue = tokenizeCleanedContextData.get(i);
                }catch (Exception exception){
                    correspondingContextDataValue = null;
                }
                String placeHolder = token.replaceAll(StringValues.BRACE_PATTERN, StringValues.EMPTY_STRING).trim();
                result.put(placeHolder, correspondingContextDataValue);
            }
        }
        return result;
    }

    public static Object[] getConvertedMethodParameterArgument(Map<String, String> valueMap, Method method, ExportableUssdMethodParameter exportableUssdMethodParameter){
        Parameter[] parameters = method.getParameters();
        if(parameters.length > 0) {
            Object[] convertedMethodParameterArgument = getNullInitializedArray(parameters.length);
            List<Parameter> parameterList = Arrays.asList(parameters);
            for(Parameter parameter : parameters){
                fillForUssdParamAnnotation(parameter, parameterList, convertedMethodParameterArgument, valueMap);
                fillForSessionValueAnnotation(parameter, parameterList, convertedMethodParameterArgument, exportableUssdMethodParameter.getSessionData());
                fillForNonAnnotationBasedExportable(parameter, parameterList, convertedMethodParameterArgument, exportableUssdMethodParameter);
            }
            return convertedMethodParameterArgument;
        }
        return new Object[]{};
    }

    private static void fillForUssdParamAnnotation(Parameter parameter, List<Parameter> parameters, Object[] values, Map<String, String> map){
        UssdParam ussdParam = parameter.getAnnotation(UssdParam.class);
        if(Objects.nonNull(ussdParam)){
            String value = ussdParam.value();
            int paramIndex = parameters.indexOf(parameter);
            if(Objects.nonNull(value) && !value.trim().isEmpty()) {
                values[paramIndex] = conversionService.convert(map.get(value), parameter.getType());
            }else {
                values[paramIndex] = conversionService.convert(map.get(parameter.getName()), parameter.getType());
            }
        }
    }

    private static void fillForSessionValueAnnotation(Parameter parameter, List<Parameter> parameters, Object[] values, SessionData sessionData){
        SessionValue sessionValue = parameter.getAnnotation(SessionValue.class);
        Map<Object, Object> map = sessionData.getSessionRepo();
        if(Objects.nonNull(sessionValue)){
            String value = sessionValue.value();
            int paramIndex = parameters.indexOf(parameter);
            if(Objects.nonNull(value) && !value.trim().isEmpty()) {
                values[paramIndex] = conversionService.convert(map.get(value), parameter.getType());
            }else {
                values[paramIndex] = conversionService.convert(map.get(parameter.getName()), parameter.getType());
            }
        }
    }

    private static void fillForNonAnnotationBasedExportable(Parameter parameter, List<Parameter> parameters, Object[] values, ExportableUssdMethodParameter exportableUssdMethodParameter){
        if(parameter.getType().isAssignableFrom(SessionData.class)){
            values[parameters.indexOf(parameter)] = exportableUssdMethodParameter.getSessionData();
        }
        else if(parameter.getType().isAssignableFrom(UserUssdContext.class)){
            values[parameters.indexOf(parameter)] = exportableUssdMethodParameter.getUserUssdContext();
        }
        else if(parameter.getType().isAssignableFrom(UssdModel.class)){
            values[parameters.indexOf(parameter)] = exportableUssdMethodParameter.getUssdModel();
        }
        else if(parameter.getType().isAssignableFrom(UssdSession.class)){
            values[parameters.indexOf(parameter)] = exportableUssdMethodParameter.getSession();
        }
    }

    private static UserUssdContext buildUserUssdContext(UssdUserExecutionContext ussdUserExecutionContext){
        return UserUssdContext.builder()
                .shortCodeContext(ussdUserExecutionContext.isShortCodeContext())
                .contextData(ussdUserExecutionContext.getContextData())
                .ussdCode(ussdUserExecutionContext.getUssdCode())
                .input(ussdUserExecutionContext.getInput())
                .isStartingSession(ussdUserExecutionContext.isStartingSession())
                .msisdn(ussdUserExecutionContext.getMsisdn())
                .invocationType(ussdUserExecutionContext.getInvocationType())
                .prefix(ussdUserExecutionContext.getPrefix())
                .telco(ussdUserExecutionContext.getTelco())
                .build();
    }

    private static Object[] getNullInitializedArray(int size){
        return new Object[size];
    }
}
