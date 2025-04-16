package com.quantumforge.quickdial.simulation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.quantumforge.quickdial.context.UserUssdContext;
import com.quantumforge.quickdial.session.UssdModel;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BasicSimulationRequest {
    private String ussdCode;
    private UssdSession session;
    private UssdModel model;
    private UserUssdContext context;
    private String documentMessageId;
    private boolean attachToSession = true;
}
