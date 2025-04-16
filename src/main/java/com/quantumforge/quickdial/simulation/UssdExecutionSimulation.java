package com.quantumforge.quickdial.simulation;

import com.quantumforge.quickdial.context.UssdUserExecutionContext;

public interface UssdExecutionSimulation {

    UssdUserExecutionContext basicSimulation(BasicSimulationRequest request);
}
