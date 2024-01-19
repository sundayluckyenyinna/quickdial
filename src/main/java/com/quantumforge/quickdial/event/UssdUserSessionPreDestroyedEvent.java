package com.quantumforge.quickdial.event;

import com.quantumforge.quickdial.session.UssdSession;
import lombok.Getter;

@Getter
public class UssdUserSessionPreDestroyedEvent extends UssdApplicationEvent {

    public UssdUserSessionPreDestroyedEvent(UssdSession source) {
        super(source);
    }
}
