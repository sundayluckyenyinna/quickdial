package com.quantumforge.quickdial.messaging.template.engine;

import com.quantumforge.quickdial.annotation.UssdSubMenuMapping;
import com.quantumforge.quickdial.bank.global.ApplicationItem;
import com.quantumforge.quickdial.bank.global.UssdBasicItemStore;
import com.quantumforge.quickdial.bootstrap.CommonUssdConfigProperties;
import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.context.UssdExecutionContext;
import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.event.UssdEventPublisher;
import com.quantumforge.quickdial.exception.TemplateParsingException;
import com.quantumforge.quickdial.exception.UssdMessageNotFoundException;
import com.quantumforge.quickdial.interceptor.UssdInputValidationInterceptor;
import com.quantumforge.quickdial.messaging.builder.MessageSourceDocumentBuilder;
import com.quantumforge.quickdial.messaging.starter.QuickDialMessageSourceStarter;
import com.quantumforge.quickdial.messaging.template.instrumentation.OptionLineWriter;
import com.quantumforge.quickdial.messaging.template.resolvers.TemplateResolverRouter;
import com.quantumforge.quickdial.messaging.template.strut.Message;
import com.quantumforge.quickdial.messaging.template.strut.MessageDocument;
import com.quantumforge.quickdial.messaging.template.strut.MessageLine;
import com.quantumforge.quickdial.session.UssdModel;
import com.quantumforge.quickdial.session.UssdSession;
import com.quantumforge.quickdial.util.GeneralUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.Objects;

import static com.quantumforge.quickdial.messaging.config.QuickDialMessageSourceConfigDefaultProperties.*;


@Slf4j
public final class ModelUssdMessageResolver{


    private final MessageDocument messageDocument;
    private final TemplateResolverRouter templateResolverRouter;
    private final UssdModel ussdModel;
    private final String preferredEngine;
    private final UssdBasicItemStore ussdBasicItemStore;
    private final CommonUssdConfigProperties ussdConfigProperties;



    public ModelUssdMessageResolver(MessageDocumentResolverBuildItem buildItem, UssdModel ussdModel){
        this.messageDocument = buildItem.getMessageDocument();
        this.templateResolverRouter = buildItem.getTemplateResolverRouter();
        this.ussdModel = ussdModel;
        this.preferredEngine = buildItem.getPreferredEngine();
        this.ussdBasicItemStore = buildItem.getUssdBasicItemStore();
        this.ussdConfigProperties = buildItem.getUssdConfigProperties();
    }

    public String getResolvedMessageById(String messageId){
        return getResolvedMessageById(messageId, QuickDialMessageSourceStarter.sProperties.getOptionToMessageSeparator());
    }

    public String getResolvedMessageById(String messageId, String separator){
        Message cleanedMessage = getResolvedCleanMessageById(messageId);
        configureNavigationOption(cleanedMessage, ussdModel);

        OptionLineWriter optionLineWriter = OptionLineWriter.start();
        cleanedMessage.getLines().forEach(messageLine -> optionLineWriter.addLine(messageLine.getOption(), messageLine.getText(), separator));
        String rawMsgText = optionLineWriter.join();
        GeneralUtils.doIf(Objects.nonNull(ussdModel.getOwnSession()), () -> {
            ussdModel.getOwnSession().getExecutionContextChain().getCurrentElement().setResultingMessage(cleanedMessage);
            UssdEventPublisher.publishUserSessionMessageUpdatedEvent(ussdModel.getOwnSession(), cleanedMessage);
        });
        return processRawMsgWithTemplateModel(rawMsgText, ussdModel.getModelMap());
    }

    public Message getResolvedCleanMessageById(String messageId){
        assert this.messageDocument != null;
        GeneralUtils.doIf(Objects.nonNull(ussdModel) && Objects.isNull(ussdModel.getObject(UssdInputValidationInterceptor.TEMPLATE_ERROR_KEY)), () -> ussdModel.addObject(UssdInputValidationInterceptor.TEMPLATE_ERROR_KEY, false));
        Message message = this.messageDocument.getMessages().stream()
                .filter(msg -> msg.getId().equalsIgnoreCase(messageId))
                .findFirst()
                .orElseThrow(() -> new UssdMessageNotFoundException(String.format("No ussd message with id = %s was found in document with name = %s and qualified name = %s!", messageId, messageDocument.getFileName(), messageDocument.getQualifiedName())));
        String rawTaggedMessage = message.getRawTaggedMessage();
        String rawResolvedTaggedMessage = processRawMsgWithTemplateModel(rawTaggedMessage, ussdModel.getModelMap());
        rawResolvedTaggedMessage = cleanRawMessageTemplate(rawResolvedTaggedMessage);
        Document parsedDocument  = Jsoup.parse(rawResolvedTaggedMessage, Parser.xmlParser());
        Element messageElement = parsedDocument.child(0);
        return Message.buildSimilarCleanedMessage(message, messageElement);
    }

    private void configureNavigationOption(Message message, UssdModel model){
        if(!GeneralUtils.isNullOrEmpty(message) && !GeneralUtils.isNullOrEmpty(model)){
            UssdSession session = model.getOwnSession();
            if(!GeneralUtils.isNullOrEmpty(session)){
                UssdUserExecutionContext ussdUserExecutionContext = session.getExecutionContextChain().getCurrentElement();
                if(!GeneralUtils.isNullOrEmpty(ussdUserExecutionContext)){
                    UssdExecutionContext ussdExecutionContext = ussdUserExecutionContext.getExecutionContext();
                    UssdSubMenuMapping subMenuHandler = ussdExecutionContext.getUssdSubMenuMapping();
                    if(!GeneralUtils.isNullOrEmpty(subMenuHandler) && !subMenuHandler.hideNavigationOptions()){
                        if(!subMenuHandler.hideBackwardNavOption() || !subMenuHandler.hideForwardNavOption()) {
                            for (int i = 0; i < subMenuHandler.navOptionTopPadding(); i++) {
                                MessageLine emptyLine = new MessageLine();
                                emptyLine.getAttributes().put(XML_MESSAGE_OPTION, StringValues.EMPTY_STRING);
                                emptyLine.setText(StringValues.EMPTY_STRING);
                                message.getLines().add(emptyLine);
                            }
                            GeneralUtils.doIf(!subMenuHandler.hideBackwardNavOption(), () -> configureGoBackNavOption(message));
                            GeneralUtils.doIf(!subMenuHandler.hideForwardNavOption(), () -> configureGoForwardNavOption(message));
                        }
                    }
                }
            }
        }
    }

    private void configureGoBackNavOption(Message message){
        MessageLine goBackLine = new MessageLine();
        goBackLine.setText(ussdConfigProperties.getGoBackMenuText());
        goBackLine.getAttributes().put(XML_MESSAGE_OPTION, ussdBasicItemStore.getItem(ApplicationItem.USSD_GO_BACK_OPTION));
        message.getLines().add(goBackLine);
    }

    private void configureGoForwardNavOption(Message message){
        MessageLine goBackLine = new MessageLine();
        goBackLine.setText(ussdConfigProperties.getGoForwardMenuText());
        goBackLine.getAttributes().put(XML_MESSAGE_OPTION, ussdBasicItemStore.getItem(ApplicationItem.USSD_GO_FORWARD_OPTION));
        message.getLines().add(goBackLine);
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

    private static String cleanRawMessageTemplate(String rawMessageTemplate){
        return rawMessageTemplate.replaceFirst(XML_LINE_TAG_RAW, StringValues.EMPTY_STRING)
                .replaceFirst(XML_LINE_TAGE_RAW_SELF_CLOSING, StringValues.EMPTY_STRING);
    }
}
