package com.quantumforge.quickdial.bank.transit.factory;

import com.quantumforge.quickdial.context.UssdExecutable;
import com.quantumforge.quickdial.context.UssdExecutionContext;
import com.quantumforge.quickdial.exception.NoUssdMappingContextRegistrarFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleUssdMappingContextRegistrationFactory implements UssdMappingContextRegistrationFactory{

    private final List<UssdMappingContextRegistrar> ussdMappingContextRegistrars;

    @Override
    public void registerExecutableMapping(UssdExecutionContext ussdExecutionContext, List<UssdExecutable> executables) {
        getSupportingRegistrar(ussdExecutionContext).registerUssdMappingContext(ussdExecutionContext, executables);
    }

    private UssdMappingContextRegistrar getSupportingRegistrar(UssdExecutionContext ussdExecutionContext){
        return ussdMappingContextRegistrars
                .stream()
                .filter(ussdMappingContextRegistrar -> ussdMappingContextRegistrar.supports(ussdExecutionContext.getParentExecutionType()))
                .findFirst()
                .orElseThrow(() -> new NoUssdMappingContextRegistrarFoundException(ussdExecutionContext.getUssdMapping()));
    }
}
