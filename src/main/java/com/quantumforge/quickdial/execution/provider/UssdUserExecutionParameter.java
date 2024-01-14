package com.quantumforge.quickdial.execution.provider;

import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UssdUserExecutionParameter {
    private UssdUserExecutionContext finalUssdUserExecutionContext;
    private UssdSession ussdSession;
    private String sessionId;
}
