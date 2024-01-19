package com.quantumforge.quickdial.event;

import com.quantumforge.quickdial.session.UssdSession;
import lombok.Getter;

@Getter
public class UssdUserSessionInitializedEvent extends UssdApplicationEvent {
    private final SessionInitData sessionInitData;

    public UssdUserSessionInitializedEvent(UssdSession source, SessionInitData sessionInitData) {
        super(source);
        this.sessionInitData = sessionInitData;
    }

}
