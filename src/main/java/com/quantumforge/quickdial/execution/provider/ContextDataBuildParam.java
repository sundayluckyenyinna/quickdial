package com.quantumforge.quickdial.execution.provider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContextDataBuildParam {

    private String prefix;
    private String msisdn;
    private String telco;
    private String baseCode;
    private String originatingUssdCode;
    private String incomingInput;
    private String oldContextData;
}
