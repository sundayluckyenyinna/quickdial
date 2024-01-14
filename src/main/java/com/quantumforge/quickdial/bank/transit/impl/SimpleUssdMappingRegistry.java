package com.quantumforge.quickdial.bank.transit.impl;

import com.quantumforge.quickdial.bank.transit.UssdMappingRegistry;
import com.quantumforge.quickdial.bank.transit.factory.UssdMappingContextProviderFactory;
import com.quantumforge.quickdial.bank.transit.factory.UssdMappingContextRegistrationFactory;
import com.quantumforge.quickdial.bootstrap.CommonUssdConfigProperties;
import com.quantumforge.quickdial.context.*;
import com.quantumforge.quickdial.exception.AmbiguousUssdMappingException;
import com.quantumforge.quickdial.exception.InvalidRuntimeGroupIdException;
import com.quantumforge.quickdial.exception.NoUssdMappingFoundException;
import com.quantumforge.quickdial.util.QuickDialUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Configuration
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
    private static final List<UssdExecutable> USSD_EXECUTION_CONTEXTS = Collections.synchronizedList(new ArrayList<>());


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
        mappingContextRegistrationFactory.registerExecutableMapping(ussdExecutionContext, USSD_EXECUTION_CONTEXTS);
        validateUniqueMappingConstraints(ussdExecutionContext.getUssdMapping());
    }

    @Override
    public GroupUssdExecutableContextWrapper getExecutableByGroupId(String groupId){
        return getGroupExecutableByGroupId(groupId);
    }

    public List<UssdExecutable> getRegisteredExecutables(){
        return USSD_EXECUTION_CONTEXTS;
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

    public List<UssdExecutable> getAllWithSimilarMapping(String mapping){
        return USSD_EXECUTION_CONTEXTS.stream()
                .filter(ussdExecutable -> {
            String ussdMapping;
            if(ussdExecutable.supportExecutableType(UssdExecutableType.SOLE_EXECUTABLE)){
                ussdMapping = ((SoleUssdExecutionContextWrapper)ussdExecutable).getUssdExecutionContext().getUssdMapping();
            }else{
                ussdMapping = ((GroupUssdExecutableContextWrapper)ussdExecutable).getCommonUssdMapping();
            }
            boolean compare = quickDialUtil.getTokensBetweenDelimiters(ussdMapping).size() == quickDialUtil.getTokensBetweenDelimiters(mapping).size();
            return ussdMapping.equalsIgnoreCase(mapping) || compare;
        }).collect(Collectors.toList());
    }

    private void validateUniqueMappingConstraints(String mapping){
        List<UssdExecutable> executables = getAllWithSimilarMapping(mapping);
        if(executables.size() > 1){
            boolean oneIsSingle = executables.stream().anyMatch(ussdExecutable -> ussdExecutable.supportExecutableType(UssdExecutableType.SOLE_EXECUTABLE));
            boolean oneThanOneGroupHasSameMapping = executables.stream().filter(ussdExecutable -> ussdExecutable.supportExecutableType(UssdExecutableType.GROUP_EXECUTABLE)).count() > 1;
            if(oneIsSingle || oneThanOneGroupHasSameMapping){
                throw new AmbiguousUssdMappingException(String.format("Ambiguous ussd mapping for code: %s", mapping));
            }
        }
    }

    @EventListener(value = ApplicationStartedEvent.class)
    public void logRegisteredUssdExecutionContext(){
        if(ussdConfigProperties.isEnableVerboseMappingLogs()) {
            log.info("");
            log.info("============================================= USSD EXECUTION CONTEXT MAPPINGS =============================================");
            USSD_EXECUTION_CONTEXTS.stream().sorted(Comparator.comparingInt(UssdExecutable::mappingLength)).forEach(ussdExecutable -> {
                boolean isLastMessage = USSD_EXECUTION_CONTEXTS.indexOf(ussdExecutable) == USSD_EXECUTION_CONTEXTS.size() - 1;
                if(ussdExecutable instanceof SoleUssdExecutionContextWrapper){
                    SoleUssdExecutionContextWrapper soleUssdExecutionContextWrapper = (SoleUssdExecutionContextWrapper) ussdExecutable;
                    UssdExecutionContext executionContext = soleUssdExecutionContextWrapper.getUssdExecutionContext();
                    logUssdExecutionContext(executionContext);
                }
                else if(ussdExecutable instanceof GroupUssdExecutableContextWrapper){
                    log.info("");
                    GroupUssdExecutableContextWrapper groupUssdExecutableContextWrapper = (GroupUssdExecutableContextWrapper) ussdExecutable;
                    log.info("             ________________________Group Mapping Context__________________________       ");
                    log.info("                                                |                                           ");
                    groupUssdExecutableContextWrapper.getUssdExecutionContexts().forEach(ussdExecutionContext -> {
                        logUssdExecutionContext(ussdExecutionContext);
                        log.info("                                                |                                           ");
                    });
                    log.info("             _______________________________________________________________________");
                    log.info("");
                }
                if (!isLastMessage) {
                    log.info("--------------------------------------------------------------------------------------------------------------------------");
                }
            });
            log.info("==========================================================================================================================");
            log.info("");
        }
    }

    private void logUssdExecutionContext(UssdExecutionContext executionContext){
        log.info("Ussd Mapping: {}", executionContext.getUssdMapping());
        log.info("Invocation Method: {}", executionContext.getInvocableMethod().getName());
        log.info("Declaring class: {}", executionContext.getCallableClass().getSimpleName().substring(0, executionContext.getCallableClass().getSimpleName().indexOf("$$EnhancerBy")));
        log.info("Spring Enhanced class: {}", executionContext.getCallableClass().getSimpleName());
        log.info("Parent execution type: {}", executionContext.getParentExecutionType());
    }
}
