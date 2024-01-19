package com.quantumforge.quickdial.event;

import com.quantumforge.quickdial.session.UssdSession;
import lombok.Getter;

@Getter
public class UssdUserSessionUpdatedEvent extends UssdApplicationEvent {
    private final SessionInitData initData;
    public UssdUserSessionUpdatedEvent(UssdSession source, SessionInitData initData) {
        super(source);
        this.initData = initData;
    }
}
