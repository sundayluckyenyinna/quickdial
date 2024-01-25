package com.quantumforge.quickdial.messaging.template.engine;

import com.quantumforge.quickdial.bank.transit.impl.SimpleUssdUserSessionRegistry;
import com.quantumforge.quickdial.exception.TemplateParsingException;
import com.quantumforge.quickdial.exception.UssdMessageNotFoundException;
import com.quantumforge.quickdial.interceptor.UssdInputValidationInterceptor;
import com.quantumforge.quickdial.messaging.starter.QuickDialMessageSourceStarter;
import com.quantumforge.quickdial.messaging.template.instrumentation.OptionLineWriter;
import com.quantumforge.quickdial.messaging.template.resolvers.TemplateResolverRouter;
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
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Getter
public final class DefaultUssdMessageDocumentResolver implements UssdMessageDocumentResolver{

    private final MessageDocument messageDocument;
    private TemplateResolverRouter templateResolverRouter;
    private UssdModel ussdModel;
    private String preferredEngine;

    private DefaultUssdMessageDocumentResolver(){
        this.messageDocument = null;
    }

    public DefaultUssdMessageDocumentResolver(MessageDocument messageDocument){
        this.messageDocument = messageDocument;
        this.ussdModel = new UssdModel(null);
        this.templateResolverRouter = null;
        this.preferredEngine = null;
    }

    public DefaultUssdMessageDocumentResolver(MessageDocumentResolverBuildItem buildItem){
        this.messageDocument = buildItem.getMessageDocument();
        this.ussdModel = new UssdModel(null);
        this.templateResolverRouter = buildItem.getTemplateResolverRouter();
        this.preferredEngine = buildItem.getPreferredEngine();
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
        GeneralUtils.doIf(Objects.nonNull(ussdModel) && Objects.isNull(ussdModel.getObject(UssdInputValidationInterceptor.TEMPLATE_ERROR_KEY)), () -> ussdModel.addObject(UssdInputValidationInterceptor.TEMPLATE_ERROR_KEY, false));
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
        Context context = new Context();
        model.forEach(context::setVariable);
        try{
            return this.templateResolverRouter.resolveTemplateByEngine(rawMessage, model, preferredEngine);
        }catch (Exception exception){
            log.info("Exception occurred during template parsing. Exception message is: {}", exception.getMessage());
            throw new TemplateParsingException(exception.getMessage());
        }
    }
}
