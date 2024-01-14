package com.quantumforge.quickdial.context.factory;


import com.quantumforge.quickdial.context.UssdExecutionContext;
import com.quantumforge.quickdial.context.providers.UssdContextDataProvider;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DefaultUssdContextDataProviderFactory implements UssdContextDataProviderFactory{

    private final List<UssdContextDataProvider> contextDataProviders;

    @Override
    public String provideContextData(String input, UssdExecutionContext ussdExecutionContext, UssdSession session){
        return contextDataProviders
                .stream()
                .filter(ussdContextDataProvider -> ussdContextDataProvider.supports(ussdExecutionContext.getParentExecutionType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Ussd execution type"))
                .provide(input, ussdExecutionContext, session);
    }
}
