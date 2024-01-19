package com.quantumforge.quickdial.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionInitData {
    private String contextData;
    private String ussdCode;
    private String input;
    private String msisdn;
    private String telco;
}
