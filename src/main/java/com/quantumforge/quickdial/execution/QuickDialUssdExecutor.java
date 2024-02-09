package com.quantumforge.quickdial.execution;

import com.quantumforge.quickdial.payload.QuickDialPayload;
import com.quantumforge.quickdial.payload.UssdExecution;

public interface QuickDialUssdExecutor {

    UssdExecution<?> submit(QuickDialPayload quickDialPayload);

    <T> UssdExecution<T> submit(QuickDialPayload quickDialPayload, Class<T> tClass);
}
