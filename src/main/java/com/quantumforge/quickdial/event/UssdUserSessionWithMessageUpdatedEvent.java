package com.quantumforge.quickdial.event;

import com.quantumforge.quickdial.messaging.template.strut.Message;
import lombok.Getter;

@Getter
public class UssdUserSessionWithMessageUpdatedEvent extends UssdApplicationEvent{
    private final Message message;

    public UssdUserSessionWithMessageUpdatedEvent(Object source, Message message) {
        super(source);
        this.message = message;
    }
}
