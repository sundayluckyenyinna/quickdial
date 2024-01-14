package com.quantumforge.quickdial.messaging.template.engine;

import com.quantumforge.quickdial.bank.global.ApplicationItem;
import com.quantumforge.quickdial.bank.global.ApplicationStore;
import com.quantumforge.quickdial.messaging.template.strut.Message;
import com.quantumforge.quickdial.messaging.template.strut.MessageDocument;
import com.quantumforge.quickdial.messaging.template.strut.MessageDocuments;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MessageSourceContextManager {

    private final ApplicationStore applicationStore;

    public MessageDocument getMessageDocumentByFileName(String fileName){
        return this.getMessageDocuments().getMessageDocuments()
                .stream()
                .filter(messageDocument -> messageDocument.getFileName().equalsIgnoreCase(fileName))
                .findFirst()
                .orElse(null);
    }

    public Message getMessageByDocumentFileNameAndMessageId(String fileName, String messageId){
        MessageDocument messageDocument = this.getMessageDocumentByFileName(fileName);
        if(Objects.nonNull(messageDocument)){
            return messageDocument.getMessages()
                    .stream()
                    .filter(message -> message.getId().equalsIgnoreCase(messageId))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public MessageDocuments getMessageDocuments(){
        return applicationStore.getItem(ApplicationItem.MESSAGE_DOCUMENTS.name(), MessageDocuments.class);
    }
}
