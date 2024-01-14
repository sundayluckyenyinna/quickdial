package com.quantumforge.quickdial.bank.transit.factory;

import com.quantumforge.quickdial.context.UssdExecutable;
import com.quantumforge.quickdial.context.UssdExecutionContext;

import java.util.List;

public interface UssdMappingContextRegistrationFactory {

    void registerExecutableMapping(UssdExecutionContext ussdExecutionContext, List<UssdExecutable> executables);
}
