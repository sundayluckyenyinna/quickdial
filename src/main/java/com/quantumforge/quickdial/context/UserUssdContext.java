package com.quantumforge.quickdial.context;

import com.quantumforge.quickdial.execution.provider.UssdInvocationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class UserUssdContext {
    private String contextData;
    private String ussdCode;
    private String input;
    private String msisdn;
    private String telco;
    private String prefix;
    private UssdInvocationType invocationType;
    private boolean isStartingSession;
    private boolean shortCodeContext;
}
