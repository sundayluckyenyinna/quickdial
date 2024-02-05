package com.quantumforge.quickdial.event;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfiguration
@Import(value = {
        UssdEventPublisher.class
})
public class EventModule {
}
