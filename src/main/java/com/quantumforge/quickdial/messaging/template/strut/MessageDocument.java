package com.quantumforge.quickdial.messaging.template.strut;

import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
public class MessageDocument {

    private File file;
    private String fileName;
    private String qualifiedName;
    private List<Message> messages;

    private MessageDocument(){

    }

    public MessageDocument(String fileName){
        this.fileName = fileName;
        this.messages = new ArrayList<>();
    }

    public MessageDocument(String fileName, List<Message> messages){
        this.fileName = fileName;
        this.messages = messages;
    }

    public MessageDocument(FileResource fileResource){
        this.fileName = fileResource.getFile().getName();
        this.file = fileResource.getFile();
        this.qualifiedName = fileResource.getQualifiedName();
        this.messages = new ArrayList<>();
    }
}
