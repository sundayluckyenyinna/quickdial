package com.quantumforge.quickdial.bank;

import com.quantumforge.quickdial.bank.global.SimpleApplicationStore;
import com.quantumforge.quickdial.bank.global.UssdBasicItemStore;
import com.quantumforge.quickdial.bank.transit.factory.*;
import com.quantumforge.quickdial.bank.transit.impl.SimpleUssdEventRegistry;
import com.quantumforge.quickdial.bank.transit.impl.SimpleUssdMappingRegistry;
import com.quantumforge.quickdial.bank.transit.impl.SimpleUssdUserSessionRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfiguration
@Import(value = {
        SimpleApplicationStore.class,
        UssdBasicItemStore.class,
        DirectUssdMapTypeContextContextProvider.class,
        GroupUssdMappingContextRegistrar.class,
        ParamUssdMapTypeContextProvider.class,
        ShortCodeUssdMapTypeContextProvider.class,
        SimpleUssdMappingContextProviderFactory.class,
        SimpleUssdMappingContextRegistrationFactory.class,
        SimpleUssdStringMappingConstructor.class,
        SoleUssdMappingContextRegistrar.class,
        SimpleUssdEventRegistry.class,
        SimpleUssdMappingRegistry.class,
        SimpleUssdUserSessionRegistry.class
})
public class BankModule {
}
