package com.quantumforge.quickdial.bank.transit.factory;

import com.quantumforge.quickdial.context.SoleUssdExecutionContextWrapper;
import com.quantumforge.quickdial.context.UssdExecutable;
import com.quantumforge.quickdial.context.UssdExecutableType;
import com.quantumforge.quickdial.context.UssdExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SoleUssdMappingContextRegistrar implements UssdMappingContextRegistrar{

    private final ConfigurableApplicationContext applicationContext;

    @Override
    public void registerUssdMappingContext(UssdExecutionContext ussdExecutionContext, List<UssdExecutable> executables) {
        UssdExecutionContextMatchResult contextMatchResult = findMatchedExistingUssdContext(ussdExecutionContext, executables);
        if(contextMatchResult.isExisting()){
            displayAmbiguousUssdMappingError(ussdExecutionContext, contextMatchResult.getExistingContext());
            applicationContext.close();
        }else {
           executables.add(new SoleUssdExecutionContextWrapper(ussdExecutionContext));
        }
    }

    @Override
    public boolean supports(UssdExecutableType executableType) {
        return executableType == UssdExecutableType.SOLE_EXECUTABLE;
    }
}
