package com.quantumforge.quickdial.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SoleUssdExecutionContextWrapper implements UssdExecutable{
    private final UssdExecutionContext ussdExecutionContext;

    public SoleUssdExecutionContextWrapper(UssdExecutionContext ussdExecutionContext) {
        this.ussdExecutionContext = ussdExecutionContext;
        this.ussdExecutionContext.setPossessLock(true);  // A single ussd context always possess the lock to make it worthy for execution.
    }

    @Override
    public boolean supportExecutableType(UssdExecutableType executableType) {
        return executableType == UssdExecutableType.SOLE_EXECUTABLE;
    }

    @Override
    public UssdExecutableType getExecutableType() {
        return UssdExecutableType.SOLE_EXECUTABLE;
    }

    @Override
    public int mappingLength() {
        return ussdExecutionContext.getUssdMapping().length();
    }
}
