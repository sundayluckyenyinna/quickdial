package com.quantumforge.quickdial.messaging.template.instrumentation;

import com.quantumforge.quickdial.common.StringValues;

public class CompositeLineWriter extends AbstractLineWriter{

    public CompositeLineWriter addLine(String line){
        lines.add(line);
        return this;
    }

    public CompositeLineWriter addLine(String option, String message, String separator){
        String completeLine = String.join(separator, option, message);
        lines.add(completeLine);
        return this;
    }

    public CompositeLineWriter addLine(String option, String message){
        return addLine(option, message, StringValues.SINGLE_SPACE);
    }
}
