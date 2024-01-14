package com.quantumforge.quickdial.execution;

import com.quantumforge.quickdial.execution.provider.UssdUserExecutionParameter;
import com.quantumforge.quickdial.payload.QuickDialPayload;
import com.quantumforge.quickdial.payload.UssdExecution;

public interface QuickDialUssdExecutor {

    <T> T submit(QuickDialPayload quickDialPayload);
}
