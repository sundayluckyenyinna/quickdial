package com.quantumforge.quickdial.stats.model;

import com.quantumforge.quickdial.execution.provider.UssdInvocationType;
import lombok.Data;

@Data
public class UssdUserExecutionContextModel {
    private UssdExecutionContextModel executionContext;
    private String contextData;
    private String ussdCode;
    private String input;
    private String msisdn;
    private String telco;
    private String prefix;
    private UssdInvocationType invocationType;
    private boolean isStartingSession;
    private boolean isShortCodeContext;
}
