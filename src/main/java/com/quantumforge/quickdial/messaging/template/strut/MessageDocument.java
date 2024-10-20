package com.quantumforge.quickdial.messaging.template.strut;

import lombok.Data;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Data
public class MessageDocument {

    private String fileName;
    private String resourceFilePath;
    private InputStream inputStream;
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
        this.fileName = fileResource.getFileName();
        this.resourceFilePath = fileResource.getResourceFilePath();
        this.inputStream = fileResource.getInputStream();
        this.qualifiedName = fileResource.getQualifiedName();
        this.messages = new ArrayList<>();
    }
}
