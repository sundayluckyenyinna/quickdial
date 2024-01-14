package com.quantumforge.quickdial.execution;

import com.quantumforge.quickdial.context.UserUssdContext;
import com.quantumforge.quickdial.session.SessionData;
import com.quantumforge.quickdial.session.UssdModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportableUssdMethodParameter {
    private SessionData sessionData;
    private UserUssdContext userUssdContext;
    private UssdModel ussdModel;
}
