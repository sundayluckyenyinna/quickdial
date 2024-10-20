package com.quantumforge.quickdial.messaging.builder.impl;

import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.execution.result.UssdRedirectConfigProperties;
import com.quantumforge.quickdial.messaging.builder.DocumentType;
import com.quantumforge.quickdial.messaging.builder.MessageSourceDocumentBuilder;
import com.quantumforge.quickdial.messaging.config.QuickDialMessageTemplateEngineConfigProperties;
import com.quantumforge.quickdial.messaging.template.resolvers.ModelTemplateEngine;
import com.quantumforge.quickdial.messaging.template.strut.FileResource;
import com.quantumforge.quickdial.messaging.template.strut.Message;
import com.quantumforge.quickdial.messaging.template.strut.MessageDocument;
import com.quantumforge.quickdial.messaging.template.strut.MessageLine;
import com.quantumforge.quickdial.util.GeneralUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
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
    private final QuickDialMessageTemplateEngineConfigProperties templateEngineConfigProperties;

    @Override
    public boolean supportsDocumentType(DocumentType documentType) {
        return documentType == DocumentType.XML;
    }

    @Override
    @SneakyThrows
    public MessageDocument buildDocument(FileResource fileResource){
        MessageDocument messageDocument = new MessageDocument(fileResource);
        Document document = Jsoup.parse(fileResource.getInputStream(), StandardCharsets.UTF_8.name(), StringValues.EMPTY_STRING, Parser.xmlParser());
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
            Document documentContainingOnlyErrorLine = Jsoup.parse(getAutomaticErrorLineTemplate(), Parser.xmlParser());
            Document rawMessageDocument = Jsoup.parse(message.getRawTaggedMessage(), Parser.xmlParser());
            Element rawMessageElement = rawMessageDocument.child(0);
            Element errorLineElement = documentContainingOnlyErrorLine.getElementsByTag(XML_LINE_TAG).first();
            assert errorLineElement != null;
            rawMessageElement.prependChild(errorLineElement);

            MessageLine errorMessageLine = new MessageLine();
            String errorContent = errorLineElement.text();
            errorMessageLine.getAttributes().put(XML_MESSAGE_OPTION, StringValues.EMPTY_STRING);
            errorMessageLine.setText(errorContent);
            message.getLines().add(0, errorMessageLine);
            String rawMessageTag = cleanRawMessage(rawMessageElement.toString());
            message.setRawTaggedMessage(rawMessageTag);
        }
    }

    private String getAutomaticErrorLineTemplate(){
        String template;
        ModelTemplateEngine engine = getPreferredTemplateEngine();
        switch (engine){
            case THYMELEAF: { template = THYMELEAF_AUTOMATIC_ERROR_LINE_TEMPLATE; break; }
            case FREEMARKER: { template = FREEMARKER_AUTOMATIC_ERROR_LINE_TEMPLATE; break; }
            default: template = THYMELEAF_AUTOMATIC_ERROR_LINE_TEMPLATE;
        }
        String redirectMessage = redirectConfigProperties.getDefaultInputValidationMessage();
        redirectMessage = formatRedirectionMessageTagByEngine(redirectMessage, engine);
        String templateWithMessage = String.format(template, redirectMessage);
        return Parser.unescapeEntities(templateWithMessage, false);
    }

    private ModelTemplateEngine getPreferredTemplateEngine(){
        String preferredEngine = templateEngineConfigProperties.getPreferredEngine();
        ModelTemplateEngine templateEngine;
        try{
            templateEngine = ModelTemplateEngine.valueOf(preferredEngine.toUpperCase());
        }catch (Exception ignored){ templateEngine = ModelTemplateEngine.THYMELEAF; }
        return templateEngine;
    }

    private static String cleanRawMessage(String rawMessageElement){
        return rawMessageElement
                .replaceAll("!--", StringValues.EMPTY_STRING)
                .replaceAll("--", StringValues.EMPTY_STRING)
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">");
    }

    private String formatRedirectionMessageTagByEngine(String message, ModelTemplateEngine engine){
        String formattedMessage;
        switch (engine){
            case FREEMARKER: { formattedMessage = message.replaceAll("\\[\\[", StringValues.EMPTY_STRING).replaceAll("]]", StringValues.EMPTY_STRING); break; }
            case THYMELEAF:
            default: formattedMessage = message;
        }
        return formattedMessage;
    }
}
