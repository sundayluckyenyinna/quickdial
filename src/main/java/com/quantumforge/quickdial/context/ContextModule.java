package com.quantumforge.quickdial.context;

import com.quantumforge.quickdial.context.factory.DefaultUssdContextDataProviderFactory;
import com.quantumforge.quickdial.context.providers.GroupUssdContextDataProvider;
import com.quantumforge.quickdial.context.providers.SoleUssdContextDataProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfiguration
@Import(value = {
        DefaultUssdContextDataProviderFactory.class,
        GroupUssdContextDataProvider.class,
        SoleUssdContextDataProvider.class
})
public class ContextModule {
}
