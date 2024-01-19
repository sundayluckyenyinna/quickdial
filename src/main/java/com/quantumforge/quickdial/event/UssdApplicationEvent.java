package com.quantumforge.quickdial.event;

import org.springframework.context.ApplicationEvent;

public abstract class UssdApplicationEvent extends ApplicationEvent {
    public UssdApplicationEvent(Object source) {
        super(source);
    }
}
