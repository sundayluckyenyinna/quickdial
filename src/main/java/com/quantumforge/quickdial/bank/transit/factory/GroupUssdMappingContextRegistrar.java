package com.quantumforge.quickdial.bank.transit.factory;

import com.quantumforge.quickdial.bank.transit.impl.SimpleUssdMappingRegistry;
import com.quantumforge.quickdial.context.GroupUssdExecutableContextWrapper;
import com.quantumforge.quickdial.context.UssdExecutable;
import com.quantumforge.quickdial.context.UssdExecutableType;
import com.quantumforge.quickdial.context.UssdExecutionContext;
import com.quantumforge.quickdial.exception.AmbiguousUssdMappingException;
import com.quantumforge.quickdial.exception.IllegalGroupUssdMappingRegistrationException;
import com.quantumforge.quickdial.util.GeneralUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.quantumforge.quickdial.util.GeneralUtils.MESSAGE_PADDING;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GroupUssdMappingContextRegistrar implements UssdMappingContextRegistrar{

    private final ConfigurableApplicationContext applicationContext;

    @Override
    public void registerUssdMappingContext(UssdExecutionContext ussdExecutionContext, List<UssdExecutable> executables) {
        UssdExecutionContextMatchResult contextMatchResult = findMatchedExistingUssdContext(ussdExecutionContext, executables);
        if(contextMatchResult.isExisting()){
            displayAmbiguousUssdMappingError(ussdExecutionContext, contextMatchResult.getExistingContext());
            applicationContext.close();
            return;
        }
        String groupId = ussdExecutionContext.getGroupMapping().id();

        GroupUssdExecutableContextWrapper existingContextWrapperByCommonMapping = SimpleUssdMappingRegistry.getExecutableByCommonUssdMapping(ussdExecutionContext.getUssdMapping());
        if( Objects.nonNull(existingContextWrapperByCommonMapping) && !existingContextWrapperByCommonMapping.getGroupId().equalsIgnoreCase(groupId)){
            throw new AmbiguousUssdMappingException("Ussd Mapping in different group must have different group mapping");
        }

        GroupUssdExecutableContextWrapper contextWrapper = SimpleUssdMappingRegistry.getExecutableByGroupId(groupId, executables, true);
        assert contextWrapper != null;
        List<UssdExecutionContext> executionContexts = contextWrapper.getUssdExecutionContexts();
        if(executionContexts.isEmpty()){
            contextWrapper.setCommonUssdMapping(ussdExecutionContext.getUssdMapping());
            ussdExecutionContext.setPossessLock(true);
        }
        else if(Objects.nonNull(contextWrapper.getCommonUssdMapping()) && !contextWrapper.getCommonUssdMapping().equalsIgnoreCase(ussdExecutionContext.getUssdMapping())){
            displayIllegalGroupMappingRegistration(contextWrapper.getCommonUssdMapping(), ussdExecutionContext);
            applicationContext.close();
        }
        executionContexts.add(ussdExecutionContext);
        executionContexts.sort(Comparator.comparingInt(executionContext -> executionContext.getGroupMapping().order()));
        contextWrapper.setUssdExecutionContexts(executionContexts);
    }

    private void displayIllegalGroupMappingRegistration(String groupCommonMapping, UssdExecutionContext incomingMapping){
        String message = "FAILED TO START";
        String reason = "Ussd Mapping in same group context must have same ussd mapping";
        String cause = String.format("Incoming mapping with mapping %s in class %s on method '%s' does not match the group context's common ussd mapping of value %s", incomingMapping.getUssdMapping(), incomingMapping.getCallableClassName(), incomingMapping.getInvocableMethod().getName(), groupCommonMapping);
        String action = "Configure the mappings in the same group context to have the same common ussd mapping";

        System.out.println(GeneralUtils.writeWithLeftPadding(MESSAGE_PADDING,"************************************************************"));
        System.out.println(GeneralUtils.writeWithLeftPadding(MESSAGE_PADDING,message));
        System.out.println(GeneralUtils.writeWithLeftPadding(MESSAGE_PADDING, reason));
        System.out.println(GeneralUtils.writeWithLeftPadding(MESSAGE_PADDING, cause));
        System.out.println(GeneralUtils.writeWithLeftPadding(MESSAGE_PADDING, action));
        System.out.println(GeneralUtils.writeWithLeftPadding(MESSAGE_PADDING,"************************************************************"));
    }

    @Override
    public boolean supports(UssdExecutableType executableType) {
        return executableType == UssdExecutableType.GROUP_EXECUTABLE;
    }
}
