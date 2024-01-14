package com.quantumforge.quickdial.bank.transit.factory;

import com.quantumforge.quickdial.bank.transit.UssdMapType;
import com.quantumforge.quickdial.context.*;
import com.quantumforge.quickdial.exception.UnknownUssdMapTypeException;
import com.quantumforge.quickdial.util.QuickDialUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleUssdMappingContextProviderFactory implements UssdMappingContextProviderFactory{

    private final QuickDialUtil quickDialUtil;
    private final List<UssdMappingContextProvider> ussdMappingContextProviders;

    @Override
    public UssdExecutionContext getMatchingContext(String mapping, List<UssdExecutable> executables){
        UssdMapType type = quickDialUtil.getUssdMapType(mapping);
        List<UssdExecutionContext> executionContexts = getUssdContextsWithUniqueMappings(executables);
        return ussdMappingContextProviders
                .stream()
                .filter(provider -> provider.supportUssdMapType(type))
                .findFirst()
                .orElseThrow(UnknownUssdMapTypeException::new)
                .getMatchingContext(mapping, executionContexts);
    }

}
