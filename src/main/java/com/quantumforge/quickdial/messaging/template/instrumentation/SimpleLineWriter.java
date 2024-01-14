package com.quantumforge.quickdial.messaging.template.instrumentation;

public class SimpleLineWriter extends AbstractLineWriter{

    private SimpleLineWriter(){}

    public SimpleLineWriter addLine(String line){
        lines.add(line);
        return this;
    }
}
