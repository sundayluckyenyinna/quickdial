package com.quantumforge.quickdial.messaging.template.strut;

import com.quantumforge.quickdial.common.StringValues;
import lombok.Getter;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

import static com.quantumforge.quickdial.messaging.config.QuickDialMessageSourceConfigDefaultProperties.XML_LINE_TAG;
import static com.quantumforge.quickdial.messaging.config.QuickDialMessageSourceConfigDefaultProperties.XML_MESSAGE_OPTION;

@Getter
public class Message {

    private String id;
    private String rawTaggedMessage;
    private Map<String, Object> attributes = new HashMap<>();
    private final List<MessageLine> lines;

    public Message(){
        this.id = UUID.randomUUID().toString();
        this.lines = new ArrayList<>();
        this.rawTaggedMessage = StringValues.EMPTY_STRING;
    }

    public Message(String id){
        this.id = id;
        this.lines = new ArrayList<>();
        this.rawTaggedMessage = StringValues.EMPTY_STRING;
    }

    public Message(String id, List<MessageLine> lines){
        this.id = id;
        this.lines = lines;
        this.rawTaggedMessage = StringValues.EMPTY_STRING;
    }

    private void setId(String id){
        this.id  = id;
    }

    private void setAttributes(Map<String, Object> attributes){
        this.attributes = attributes;
    }

    public void setRawTaggedMessage(String rawTaggedMessage){
        this.rawTaggedMessage = rawTaggedMessage;
    }

    public static Message buildSimilarCleanedMessage(Message sourceMessage, Element messageElement){
        Message message = new Message();
        message.setRawTaggedMessage(sourceMessage.getRawTaggedMessage());
        message.setId(sourceMessage.getId());
        message.setAttributes(sourceMessage.getAttributes());

        Elements lineElements = messageElement.getElementsByTag(XML_LINE_TAG);
        for(Element line : lineElements){
            MessageLine messageLine = new MessageLine();
            String option = line.attr(XML_MESSAGE_OPTION);
            messageLine.getAttributes().put(XML_MESSAGE_OPTION, option);

            messageLine.setText(line.text());
            message.getLines().add(messageLine);
        }
        return message;
    }
}
