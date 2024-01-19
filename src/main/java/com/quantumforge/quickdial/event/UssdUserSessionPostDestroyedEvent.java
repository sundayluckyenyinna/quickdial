package com.quantumforge.quickdial.event;

import com.quantumforge.quickdial.session.UssdSession;
import lombok.Getter;

@Getter
public class UssdUserSessionPostDestroyedEvent extends UssdApplicationEvent {
    public UssdUserSessionPostDestroyedEvent() {
        super(new UssdSession());
    }
}
