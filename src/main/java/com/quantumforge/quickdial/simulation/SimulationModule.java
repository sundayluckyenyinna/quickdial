package com.quantumforge.quickdial.simulation;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfiguration
@Import({
        BasicUssdExecutionSimulation.class
})
public class SimulationModule {
}
