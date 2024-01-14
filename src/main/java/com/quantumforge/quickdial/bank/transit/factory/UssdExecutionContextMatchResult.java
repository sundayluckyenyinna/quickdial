package com.quantumforge.quickdial.bank.transit.factory;

import com.quantumforge.quickdial.context.UssdExecutionContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UssdExecutionContextMatchResult {
    private boolean isExisting = false;
    private UssdExecutionContext existingContext;
}
