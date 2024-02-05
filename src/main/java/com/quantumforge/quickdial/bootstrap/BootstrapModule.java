package com.quantumforge.quickdial.bootstrap;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfiguration
@Import(value = { UssdMappingRegistryBootstrap.class })
public class BootstrapModule {
}
