package com.quantumforge.quickdial.payload;

import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.execution.provider.UssdInvocationType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class QuickDialPayload {
    private String sessionId;
    private String msisdn;
    private String telco;
    private String originatingCode;
    private String prefix = StringValues.EMPTY_STRING;
    private String input = StringValues.EMPTY_STRING;
    private UssdInvocationType invocationType = UssdInvocationType.PROGRESSIVE;
    private boolean shortCodeString;
    private boolean sessionStarting;
}