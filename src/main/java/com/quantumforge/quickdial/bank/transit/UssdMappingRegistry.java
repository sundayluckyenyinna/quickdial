package com.quantumforge.quickdial.bank.transit;

import com.quantumforge.quickdial.context.GroupUssdExecutableContextWrapper;
import com.quantumforge.quickdial.context.UssdExecutable;
import com.quantumforge.quickdial.context.UssdExecutionContext;

public interface UssdMappingRegistry {

    UssdExecutionContext getMatchingUssdExecutionContextForMapping(String mapping);

    void registerUssdMapping(UssdExecutionContext ussdExecutionContext);

    GroupUssdExecutableContextWrapper getExecutableByGroupId(String groupId);
}
