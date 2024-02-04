package com.quantumforge.quickdial.stats.model;

import com.quantumforge.quickdial.messaging.template.strut.Message;
import lombok.Data;

import java.util.List;

@Data
public class UssdMessageDocumentModel {
    private String file;
    private String fileName;
    private String qualifiedName;
    private List<Message> messages;
}
