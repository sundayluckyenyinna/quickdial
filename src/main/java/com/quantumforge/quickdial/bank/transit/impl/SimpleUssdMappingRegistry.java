package com.quantumforge.quickdial.bank.transit.impl;

import com.quantumforge.quickdial.util.QColor;
import com.quantumforge.quickdial.bank.transit.UssdMappingRegistry;
import com.quantumforge.quickdial.bank.transit.factory.UssdMappingContextProviderFactory;
import com.quantumforge.quickdial.bank.transit.factory.UssdMappingContextRegistrationFactory;
import com.quantumforge.quickdial.bootstrap.CommonUssdConfigProperties;
import com.quantumforge.quickdial.context.*;
import com.quantumforge.quickdial.event.UssdEventPublisher;
import com.quantumforge.quickdial.exception.AmbiguousUssdMappingException;
import com.quantumforge.quickdial.exception.InvalidRuntimeGroupIdException;
import com.quantumforge.quickdial.exception.NoUssdMappingFoundException;
import com.quantumforge.quickdial.logger.QuickDialLogger;
import com.quantumforge.quickdial.util.QuickDialUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
@Configuration
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(CommonUssdConfigProperties.class)
public class SimpleUssdMappingRegistry implements UssdMappingRegistry {

    private final QuickDialUtil quickDialUtil;
    private final CommonUssdConfigProperties ussdConfigProperties;
    private final UssdMappingContextProviderFactory mappingContextProviderFactory;
    private final UssdMappingContextRegistrationFactory mappingContextRegistrationFactory;


    /**
     * The container for all registered ussd execution contexts.
     */
    public static final List<UssdExecutable> USSD_EXECUTION_CONTEXTS = Collections.synchronizedList(new ArrayList<>());


    /**
     * Returns the UssdExecutionContext that matches the ussd mapping
     * @param mapping: String
     * @return UssdExecutionContext
     */
    @Override
    public UssdExecutionContext getMatchingUssdExecutionContextForMapping(String mapping){
        return Optional.ofNullable(mappingContextProviderFactory.getMatchingContext(mapping, USSD_EXECUTION_CONTEXTS))
                .orElseThrow(() -> new NoUssdMappingFoundException(mapping));
    }

    @Override
    public void registerUssdMapping(UssdExecutionContext ussdExecutionContext){
        ensureUniqueMappingConstraints(ussdExecutionContext);
        mappingContextRegistrationFactory.registerExecutableMapping(ussdExecutionContext, USSD_EXECUTION_CONTEXTS);
    }

    @Override
    public GroupUssdExecutableContextWrapper getExecutableByGroupId(String groupId){
        return getGroupExecutableByGroupId(groupId);
    }

    public static GroupUssdExecutableContextWrapper getGroupExecutableByGroupId(String groupId){
        GroupUssdExecutableContextWrapper contextWrapper = getExecutableByGroupId(groupId, USSD_EXECUTION_CONTEXTS, false);
        if(Objects.isNull(contextWrapper)){
            throw new InvalidRuntimeGroupIdException(groupId);
        }
        return contextWrapper;
    }

    public static UssdExecutable getExecutableByGroupId(String groupId, List<UssdExecutable> executables){
        return executables
                .stream()
                .filter(ussdExecutable -> ussdExecutable.supportExecutableType(UssdExecutableType.GROUP_EXECUTABLE))
                .map(ussdExecutable -> (GroupUssdExecutableContextWrapper) ussdExecutable)
                .filter(groupUssdExecutableContextWrapper -> Objects.nonNull(groupUssdExecutableContextWrapper.getGroupId()))
                .filter(groupUssdExecutableContextWrapper -> groupUssdExecutableContextWrapper.getGroupId().equalsIgnoreCase(groupId))
                .findFirst()
                .orElse(null);
    }

    public static GroupUssdExecutableContextWrapper getExecutableByGroupId(String groupId, List<UssdExecutable> executables, boolean returnNewIfNotExist){
        UssdExecutable executable = getExecutableByGroupId(groupId, executables);
        if(Objects.isNull(executable) && returnNewIfNotExist){
            GroupUssdExecutableContextWrapper contextWrapper = new GroupUssdExecutableContextWrapper();
            contextWrapper.setGroupId(groupId);
            contextWrapper.setUssdExecutionContexts(new ArrayList<>());
            USSD_EXECUTION_CONTEXTS.add(contextWrapper);
            return contextWrapper;
        }
        if(Objects.isNull(executable)){ return null; }
        return (GroupUssdExecutableContextWrapper) executable;
    }

    public static GroupUssdExecutableContextWrapper getExecutableByCommonUssdMapping(String commonUssdMapping){
        return USSD_EXECUTION_CONTEXTS.stream()
                .filter(ussdExecutable -> Objects.nonNull(ussdExecutable.getExecutableType()) && ussdExecutable.supportExecutableType(UssdExecutableType.GROUP_EXECUTABLE))
                .map(ussdExecutable -> (GroupUssdExecutableContextWrapper) ussdExecutable)
                .filter(executable -> Objects.nonNull(executable.getCommonUssdMapping()) && executable.getCommonUssdMapping().equalsIgnoreCase(commonUssdMapping))
                .findFirst()
                .orElse(null);
    }

    private List<UssdExecutionContext> getAllFlatExecutionContexts(){
        List<UssdExecutionContext> result = new ArrayList<>();
        USSD_EXECUTION_CONTEXTS
                .forEach(ussdExecutable -> {
                    if (ussdExecutable.supportExecutableType(UssdExecutableType.SOLE_EXECUTABLE)){
                        UssdExecutionContext context = ((SoleUssdExecutionContextWrapper)ussdExecutable).getUssdExecutionContext();
                        result.add(context);
                    }
                    else if(ussdExecutable.supportExecutableType(UssdExecutableType.GROUP_EXECUTABLE)){
                        List<UssdExecutionContext> list = ((GroupUssdExecutableContextWrapper)ussdExecutable).getUssdExecutionContexts();
                        result.addAll(list);
                    }
                });
        return result;
    }

    private void ensureUniqueMappingConstraints(UssdExecutionContext incomingContext){
        List<UssdExecutionContext> executionContexts = getAllFlatExecutionContexts();
        AtomicReference<UssdExecutionContext> atomicReference = new AtomicReference<>();
        boolean alreadyRegistered = executionContexts.stream()
                .anyMatch(ussdExecutionContext -> {
                    boolean isSole = ussdExecutionContext.getParentExecutionType() == UssdExecutableType.SOLE_EXECUTABLE;
                    boolean compare = quickDialUtil.getTokensBetweenDelimiters(incomingContext.getUssdMapping()).size()
                                        == quickDialUtil.getTokensBetweenDelimiters(ussdExecutionContext.getUssdMapping()).size();
                    atomicReference.set(ussdExecutionContext);
                    return isSole && compare;
                });
        if(alreadyRegistered){
            UssdExecutionContext matchingContext = atomicReference.get();
            boolean isOneOfThemParameterized = quickDialUtil.isParamMapping(incomingContext.getUssdMapping()) || quickDialUtil.isParamMapping(matchingContext.getUssdMapping());
            boolean isExactlySame = incomingContext.getUssdMapping().equalsIgnoreCase(matchingContext.getUssdMapping());
            if(isOneOfThemParameterized || isExactlySame) {
                if(isAmbiguousForTwoMapping(incomingContext.getUssdMapping(), matchingContext.getUssdMapping())) {
                    String inClass = incomingContext.getCallableClass().getName();
                    String inMethod = incomingContext.getInvocableMethod().getName();
                    String matClass = matchingContext.getCallableClass().getName();
                    String matMethod = matchingContext.getInvocableMethod().getName();
                    throw new AmbiguousUssdMappingException(String.format("Ambiguous mapping for ussd mapping %s defined on method '%s' in class '%s'. A similar mapping of %s was found on method '%s' in class '%s'", incomingContext.getUssdMapping(), inMethod, inClass, matchingContext.getUssdMapping(), matMethod, matClass));
                }
            }
        }
    }

    private boolean isAmbiguousForTwoMapping(String firstMapping, String secondMapping){
        List<String> firstTokens = quickDialUtil.getTokensBetweenDelimiters(firstMapping);
        List<String> secondTokens = quickDialUtil.getTokensBetweenDelimiters(secondMapping);
        boolean isAmbiguous = false;
        for(int i = 0; i < firstTokens.size(); i++){
            String currentFirstToken = firstTokens.get(i);
            String currentSecondToken = secondTokens.get(i);
            if(currentFirstToken.equalsIgnoreCase(currentSecondToken)){
                break;
            }
            if(quickDialUtil.isParamMapping(currentFirstToken) && quickDialUtil.isDirectMapping(currentSecondToken)){
                isAmbiguous = true;
                break;
            }
            if(quickDialUtil.isDirectMapping(currentFirstToken) && quickDialUtil.isParamMapping(currentSecondToken)){
                isAmbiguous = true;
                break;
            }
        }
        return isAmbiguous;
    }

    @EventListener(value = ApplicationStartedEvent.class)
    public void logRegisteredUssdExecutionContextAndPublishEvent(){
        if(ussdConfigProperties.isEnableVerboseMappingLogs() && !USSD_EXECUTION_CONTEXTS.isEmpty()) {
            System.out.println();
            log.info("============================================= USSD EXECUTION CONTEXT MAPPINGS =============================================");
            AtomicInteger atomicInteger = new AtomicInteger(0);
            USSD_EXECUTION_CONTEXTS.stream().sorted(Comparator.comparingInt(UssdExecutable::mappingLength)).forEach(ussdExecutable -> {
                if(ussdExecutable instanceof SoleUssdExecutionContextWrapper soleUssdExecutionContextWrapper){
                    UssdExecutionContext executionContext = soleUssdExecutionContextWrapper.getUssdExecutionContext();
                    logUssdExecutionContext(executionContext);
                }
                else if(ussdExecutable instanceof GroupUssdExecutableContextWrapper groupUssdExecutableContextWrapper){
                    log.info("");
                    log.info("             ________________________Group Mapping Context__________________________       ");
                    log.info("                                                |                                           ");
                    groupUssdExecutableContextWrapper.getUssdExecutionContexts().forEach(ussdExecutionContext -> {
                        logUssdExecutionContext(ussdExecutionContext);
                        log.info("                                                |                                           ");
                    });
                    log.info("             _______________________________________________________________________");
                    log.info("");
                }
                if (atomicInteger.incrementAndGet() < USSD_EXECUTION_CONTEXTS.size()) {
                    log.info("--------------------------------------------------------------------------------------------------------------------------");
                }
            });
            log.info("==========================================================================================================================");
            System.out.println();
        }
        UssdEventPublisher.publishUssdMappingExecutionContextInitializedEvent(USSD_EXECUTION_CONTEXTS);
    }

    private void logUssdExecutionContext(UssdExecutionContext executionContext){
        QuickDialLogger.logInfo("Ussd Mapping: {}", QColor.Green, executionContext.getUssdMapping());
        QuickDialLogger.logInfo("Invocation Method: {}", QColor.Blue, executionContext.getInvocableMethod().getName());
        QuickDialLogger.logInfo("RedirectID: {}", null, executionContext.getContextId());
        QuickDialLogger.logInfo("Declaring class: {}", null, executionContext.getCallableClass().getName());
        QuickDialLogger.logInfo("Spring Enhanced class: {}", null, executionContext.getCallableClass().getName());
        QuickDialLogger.logInfo("Parent execution type: {}", QColor.Yellow, executionContext.getParentExecutionType().name());
        System.out.println();
    }
}
