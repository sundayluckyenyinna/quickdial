package com.quantumforge.quickdial.event;

import com.quantumforge.quickdial.messaging.template.strut.MessageDocuments;
import lombok.Getter;

@Getter
public class UssdMessageDocumentContainerInitializedEvent extends UssdApplicationEvent {
    public UssdMessageDocumentContainerInitializedEvent(MessageDocuments source) {
        super(source);
    }
}
