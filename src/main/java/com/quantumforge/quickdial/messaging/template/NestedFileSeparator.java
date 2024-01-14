package com.quantumforge.quickdial.messaging.template;

import com.quantumforge.quickdial.common.StringValues;

public enum NestedFileSeparator {
    UNDER_SCORE(StringValues.UNDERSCORE),
    DOT(StringValues.DOT),
    UNKNOWN(StringValues.EMPTY_STRING);

    final String value;
    NestedFileSeparator(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
