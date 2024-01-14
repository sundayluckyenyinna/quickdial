package com.quantumforge.quickdial.messaging.template.instrumentation;

import com.quantumforge.quickdial.common.StringValues;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public abstract class AbstractLineWriter {

    protected final List<String> lines = new ArrayList<>();
    public AbstractLineWriter(){}

    public String join(String delimiter){
        StringJoiner joiner = new StringJoiner(delimiter);
        lines.forEach(joiner::add);
        return joiner.toString();
    }

    public String join(){
        return join(StringValues.NEW_LINE);
    }
}
