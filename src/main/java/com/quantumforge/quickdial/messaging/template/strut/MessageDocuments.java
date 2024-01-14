package com.quantumforge.quickdial.messaging.template.strut;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MessageDocuments {

    private List<MessageDocument> messageDocuments;

    public MessageDocuments(){
        this.messageDocuments = new ArrayList<>();
    }
}
