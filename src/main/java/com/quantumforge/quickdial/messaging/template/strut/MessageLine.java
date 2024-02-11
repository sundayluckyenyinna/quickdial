package com.quantumforge.quickdial.messaging.template.strut;

import com.quantumforge.quickdial.common.StringValues;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.quantumforge.quickdial.messaging.config.QuickDialMessageSourceConfigDefaultProperties.XML_MESSAGE_OPTION;
import static com.quantumforge.quickdial.messaging.config.QuickDialMessageSourceConfigDefaultProperties.XML_MESSAGE_TAG;

@Getter
@ToString
public class MessageLine {

    private final Map<String, Object> attributes = new HashMap<>();
    private String text;

    public void setText(String text) {
        this.text = text;
    }

    public boolean hasOption(){
        Object possibleOption = attributes.get(XML_MESSAGE_OPTION);
        return Objects.nonNull(possibleOption) && !String.valueOf(possibleOption).trim().isEmpty();
    }

    public String getOption(){
        if(hasOption()){
            return String.valueOf(attributes.get(XML_MESSAGE_OPTION));
        }
        return StringValues.EMPTY_STRING;
    }
}
