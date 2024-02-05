package com.quantumforge.quickdial.stats;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfiguration
@Import(value = {
        StatisticsRestController.class, StatisticsRestService.class
})
public class StatisticsModule {
}
