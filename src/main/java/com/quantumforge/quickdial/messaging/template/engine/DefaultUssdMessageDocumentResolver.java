package com.quantumforge.quickdial.messaging.template.engine;

import com.quantumforge.quickdial.bank.transit.impl.SimpleUssdUserSessionRegistry;
import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.exception.UssdMessageNotFoundException;
import com.quantumforge.quickdial.messaging.starter.QuickDialMessageSourceStarter;
import com.quantumforge.quickdial.messaging.template.instrumentation.OptionLineWriter;
import com.quantumforge.quickdial.messaging.template.strut.Message;
import com.quantumforge.quickdial.messaging.template.strut.MessageDocument;
import com.quantumforge.quickdial.session.UssdModel;
import com.quantumforge.quickdial.session.UssdSession;
import com.quantumforge.quickdial.util.GeneralUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.quantumforge.quickdial.messaging.config.QuickDialMessageSourceConfigDefaultProperties.XML_LINE_TAG;

@Slf4j
@Getter
public final class DefaultUssdMessageDocumentResolver implements UssdMessageDocumentResolver{

    private final MessageDocument messageDocument;
    private UssdModel ussdModel;
    private final TemplateEngine templateEngine = new TemplateEngine();

    private DefaultUssdMessageDocumentResolver(){
        this.messageDocument = null;
    }
    public DefaultUssdMessageDocumentResolver(MessageDocument messageDocument){
        this.messageDocument = messageDocument;
        this.ussdModel = new UssdModel(null);
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode(TemplateMode.XML);
        templateEngine.setTemplateResolver(resolver);
    }

    @Override
    public UssdMessageDocumentResolver withModel(UssdModel ussdModel){
        this.ussdModel = ussdModel;
        return this;
    }

    @Override
    public UssdMessageDocumentResolver withSessionId(String sessionId){
        UssdSession session = SimpleUssdUserSessionRegistry.getSessionStatically(sessionId);
        if(Objects.nonNull(session)){
            this.ussdModel = session.getUssdModel();
        }
        throw new IllegalArgumentException(String.format("No session found with sessionId: %s", sessionId));
    }

    @Override
    public UssdMessageDocumentResolver withSession(@NonNull UssdSession session){
        return this.withModel(session.getUssdModel());
    }

    @Override
    public String getResolvedMessageById(String messageId){
        return getResolvedMessageById(messageId, QuickDialMessageSourceStarter.sProperties.getOptionToMessageSeparator());
    }

    @Override
    public String getResolvedMessageById(String messageId, String separator){
        assert this.messageDocument != null;
        Message message = this.messageDocument.getMessages().stream()
                .filter(msg -> msg.getId().equalsIgnoreCase(messageId))
                .findFirst()
                .orElseThrow(() -> new UssdMessageNotFoundException(String.format("No ussd message with id = %s was found in document with name = %s and qualified name = %s!", messageId, messageDocument.getFileName(), messageDocument.getQualifiedName())));
        String rawTaggedMessage = message.getRawTaggedMessage();
        String rawResolvedTaggedMessage = processRawMsgWithTemplateModel(rawTaggedMessage, ussdModel.getModelMap());
        Document parsedDocument  = Jsoup.parse(rawResolvedTaggedMessage, Parser.xmlParser());
        Element messageElement = parsedDocument.child(0);
        Message cleanedMessage = Message.buildSimilarCleanedMessage(message, messageElement);

        OptionLineWriter optionLineWriter = OptionLineWriter.start();
        cleanedMessage.getLines().forEach(messageLine -> optionLineWriter.addLine(messageLine.getOption(), messageLine.getText(), separator));
        String rawMsgText = optionLineWriter.join();
        GeneralUtils.doIf(Objects.nonNull(ussdModel) && Objects.nonNull(ussdModel.getOwnSession()), () -> ussdModel.getOwnSession().getExecutionContextChain().getCurrentElement().setResultingMessage(cleanedMessage));
        return processRawMsgWithTemplateModel(rawMsgText, ussdModel.getModelMap());
    }

    private String processRawMsgWithTemplateModel(String rawMessage, Map<String, Object> model){
        String preparedRawMessage = prepareMessageForTemplateParsing(rawMessage);
        Context context = new Context();
        model.forEach(context::setVariable);
        return this.getTemplateEngine().process(preparedRawMessage, context);
    }

    private static String prepareTextPlaceholdersForTemplateProcessing(String input) {
        Pattern pattern = Pattern.compile(StringValues.THYMELEAF_VALUE_MATCHER_PATTERN);
        Matcher matcher = pattern.matcher(input);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String placeholder = matcher.group(0);
            String replacement = StringValues.DOUBLE__OPENING_BLOCK + placeholder + StringValues.DOUBLE_CLOSING_BLOCK;
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String prepareMessageForTemplateParsing(String rawMessage){
        Document document = Jsoup.parse(rawMessage, Parser.xmlParser());
        Elements lineElements = document.getElementsByTag(XML_LINE_TAG);
        lineElements.forEach(element -> element.text(prepareTextPlaceholdersForTemplateProcessing(element.text())));
        return document.html();
    }
}
