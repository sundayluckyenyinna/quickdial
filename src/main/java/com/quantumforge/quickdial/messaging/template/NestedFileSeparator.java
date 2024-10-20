package com.quantumforge.quickdial.messaging.template;

import com.quantumforge.quickdial.common.StringValues;
import lombok.Getter;

@Getter
public enum NestedFileSeparator {
    UNDER_SCORE(StringValues.UNDERSCORE),
    DOT(StringValues.DOT),
    UNKNOWN(StringValues.EMPTY_STRING);

    final String value;
    NestedFileSeparator(String value){
        this.value = value;
    }
}
