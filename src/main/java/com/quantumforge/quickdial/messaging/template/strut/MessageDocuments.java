package com.quantumforge.quickdial.messaging.template.strut;

import com.quantumforge.quickdial.exception.UssdMessageNotFoundException;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MessageDocuments {

    private List<MessageDocument> messageDocuments;

    public MessageDocuments(){
        this.messageDocuments = new ArrayList<>();
    }

    public MessageDocument getMessageDocumentByQualifiedName(final String qualifiedName){
        return messageDocuments.stream()
                .filter(document -> document.getQualifiedName().equalsIgnoreCase(qualifiedName))
                .findFirst()
                .orElseThrow(() -> new UssdMessageNotFoundException(String.format("No message document found for qualified name: %s", qualifiedName)));
    }
    public Message getMessageDocumentByQualifiedNameAndMessageId(final String qualifiedName, final String messageId){
        return getMessageDocumentByQualifiedName(qualifiedName).getMessages().stream()
                .filter(message -> message.getId().equalsIgnoreCase(messageId))
                .findFirst()
                .orElseThrow(() -> new UssdMessageNotFoundException(String.format("No message found for document with qualified name: %s and message-id: %s", qualifiedName, messageId)));
    }
}
