package com.quantumforge.quickdial.messaging.builder;

import com.quantumforge.quickdial.common.StringValues;

public enum DocumentType {
    XML("xml"),
    JSON("json"),
    UNSUPPORTED(StringValues.EMPTY_STRING);

    final String extension;

    DocumentType(String extension){
        this.extension = extension;
    }

    public String getExtension(){
        return this.extension;
    }
}
