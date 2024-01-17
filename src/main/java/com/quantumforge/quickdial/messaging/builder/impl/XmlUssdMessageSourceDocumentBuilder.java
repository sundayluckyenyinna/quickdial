package com.quantumforge.quickdial.messaging.builder.impl;

import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.execution.result.UssdRedirectConfigProperties;
import com.quantumforge.quickdial.messaging.builder.DocumentType;
import com.quantumforge.quickdial.messaging.builder.MessageSourceDocumentBuilder;
import com.quantumforge.quickdial.messaging.template.strut.FileResource;
import com.quantumforge.quickdial.messaging.template.strut.Message;
import com.quantumforge.quickdial.messaging.template.strut.MessageDocument;
import com.quantumforge.quickdial.messaging.template.strut.MessageLine;
import com.quantumforge.quickdial.util.GeneralUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.quantumforge.quickdial.messaging.config.QuickDialMessageSourceConfigDefaultProperties.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = {UssdRedirectConfigProperties.class})
public class XmlUssdMessageSourceDocumentBuilder implements MessageSourceDocumentBuilder {

    private final UssdRedirectConfigProperties redirectConfigProperties;

    @Override
    public boolean supportsDocumentType(DocumentType documentType) {
        return documentType == DocumentType.XML;
    }

    @Override
    @SneakyThrows
    public MessageDocument buildDocument(FileResource fileResource){
        MessageDocument messageDocument = new MessageDocument(fileResource);
        Document document = Jsoup.parse(fileResource.getFile(), StandardCharsets.UTF_8.name(), StringValues.EMPTY_STRING, Parser.xmlParser());
        Elements messageElements = document.getElementsByTag(XML_MESSAGE_TAG);

        for (Element messageElement : messageElements){
            String id = GeneralUtils.returnValueOrDefaultWith(messageElement.attr(XML_MESSAGE_ID), UUID.randomUUID().toString());
            Message message = new Message(id);
            message.getAttributes().put(XML_MESSAGE_ID, id);
            message.setRawTaggedMessage(String.valueOf(messageElement));

            Elements lines = messageElement.getElementsByTag(XML_LINE_TAG);
            for(Element line : lines){
                MessageLine messageLine = new MessageLine();
                String option = line.attr(XML_MESSAGE_OPTION);
                messageLine.getAttributes().put(XML_MESSAGE_OPTION, option);

                messageLine.setText(line.text());
                message.getLines().add(messageLine);
            }
            messageDocument.getMessages().add(message);
        }
        messageDocument.getMessages().forEach(this::configureForAutomaticRedirectionError);
        return messageDocument;
    }

    private void configureForAutomaticRedirectionError(Message message){
        if(redirectConfigProperties.isEnableAutomaticErrorRedirectionMessage()) {
            Document documentContainingOnlyErrorLine = Jsoup.parse(AUTOMATIC_ERROR_LINE_TEMPLATE, Parser.xmlParser());
            Document rawMessageDocument = Jsoup.parse(message.getRawTaggedMessage(), Parser.xmlParser());
            Element rawMessageElement = rawMessageDocument.child(0);
            Element errorLineElement = documentContainingOnlyErrorLine.child(0);
            errorLineElement.text(redirectConfigProperties.getDefaultInputValidationMessage());
            rawMessageElement.prependChild(errorLineElement);

            MessageLine errorMessageLine = new MessageLine();
            String option = StringValues.EMPTY_STRING;
            String errorContent = errorLineElement.text();
            errorMessageLine.getAttributes().put(XML_MESSAGE_OPTION, option);
            errorMessageLine.setText(errorContent);
            message.getLines().add(0, errorMessageLine);
            message.setRawTaggedMessage(rawMessageElement.toString());
        }
    }
}
