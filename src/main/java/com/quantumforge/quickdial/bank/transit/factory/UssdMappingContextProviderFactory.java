package com.quantumforge.quickdial.bank.transit.factory;

import com.quantumforge.quickdial.context.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface UssdMappingContextProviderFactory {
    UssdExecutionContext getMatchingContext(String mapping, List<UssdExecutable> contexts);

    default List<UssdExecutionContext> getUssdContextsWithUniqueMappings(List<UssdExecutable> executables){
        return executables
                .stream()
                .map(ussdExecutable -> {
                    if(ussdExecutable.supportExecutableType(UssdExecutableType.SOLE_EXECUTABLE)){
                        return ((SoleUssdExecutionContextWrapper) ussdExecutable).getUssdExecutionContext();
                    }
                    else if(ussdExecutable.supportExecutableType(UssdExecutableType.GROUP_EXECUTABLE)){
                        return ((GroupUssdExecutableContextWrapper) ussdExecutable).getUssdExecutionContexts().get(0);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
