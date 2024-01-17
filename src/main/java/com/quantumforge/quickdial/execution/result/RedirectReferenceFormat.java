package com.quantumforge.quickdial.execution.result;

import com.quantumforge.quickdial.common.StringValues;
import lombok.Getter;

@Getter
public enum RedirectReferenceFormat {
    DEFAULT_REFERENCE(StringValues.REDIRECT_REFERENCE_TOKEN),
    SLASH_REFERENCE(StringValues.FORWARD_SLASH);

    private final String joiner;

    RedirectReferenceFormat(String joiner){
        this.joiner = joiner;
    }
}
