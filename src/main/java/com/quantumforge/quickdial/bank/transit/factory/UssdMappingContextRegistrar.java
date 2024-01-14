package com.quantumforge.quickdial.bank.transit.factory;

import com.quantumforge.quickdial.context.SoleUssdExecutionContextWrapper;
import com.quantumforge.quickdial.context.UssdExecutable;
import com.quantumforge.quickdial.context.UssdExecutableType;
import com.quantumforge.quickdial.context.UssdExecutionContext;
import com.quantumforge.quickdial.util.GeneralUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.quantumforge.quickdial.util.GeneralUtils.MESSAGE_PADDING;

public interface UssdMappingContextRegistrar {

    void registerUssdMappingContext(UssdExecutionContext ussdExecutionContext, List<UssdExecutable> executables);
    boolean supports(UssdExecutableType executableType);

    default UssdExecutionContextMatchResult findMatchedExistingUssdContext(UssdExecutionContext ussdExecutionContext, List<UssdExecutable> executables){
        AtomicReference<UssdExecutionContext> existingUssdExecutableContextReference = new AtomicReference<>();
        UssdExecutionContextMatchResult result = new UssdExecutionContextMatchResult();
        boolean isExistingSoleMappingWrapper = executables.stream()
                .filter(ussdExecutable -> ussdExecutable.supportExecutableType(UssdExecutableType.SOLE_EXECUTABLE))
                .map(ussdExecutable -> (SoleUssdExecutionContextWrapper)ussdExecutable)
                .anyMatch(ussdExecutable -> {
                    if(ussdExecutable.getUssdExecutionContext().equals(ussdExecutionContext)){
                        existingUssdExecutableContextReference.set(ussdExecutable.getUssdExecutionContext());
                        return true;
                    }
                    return false;
                });
        result.setExisting(isExistingSoleMappingWrapper);
        result.setExistingContext(existingUssdExecutableContextReference.get());
        return result;
    }

    default void displayAmbiguousUssdMappingError(UssdExecutionContext incomingContext, UssdExecutionContext existingContext){
        String caution = "FAILED TO START";
        String message = String.format("Ambiguous ussd mapping for %s in class '%s' on method '%s'. A similar mapping exist for %s in class '%s' on method '%s'",
                incomingContext.getUssdMapping(), incomingContext.getCallableClassName(), incomingContext.getInvocableMethod().getName(),
                existingContext.getUssdMapping(), existingContext.getCallableClassName(), existingContext.getInvocableMethod().getName());
        String action1 = "Separate the two operations on two different menus or combine the two mapping to a single parameterized mapping strategy.";
        String action2 = "If the two mappings are in different group context, ensure they have different mapping strategy.";

        System.out.println(GeneralUtils.writeWithLeftPadding(MESSAGE_PADDING,"************************************************************"));
        System.out.println(GeneralUtils.writeWithLeftPadding(MESSAGE_PADDING, caution));
        System.out.println(GeneralUtils.writeWithPadding(MESSAGE_PADDING, MESSAGE_PADDING, message));
        System.out.println(GeneralUtils.writeWithPadding(MESSAGE_PADDING, MESSAGE_PADDING, action1));
        System.out.println(GeneralUtils.writeWithPadding(MESSAGE_PADDING, MESSAGE_PADDING, action2));
        System.out.println(GeneralUtils.writeWithLeftPadding(MESSAGE_PADDING,"************************************************************"));
    }
}
