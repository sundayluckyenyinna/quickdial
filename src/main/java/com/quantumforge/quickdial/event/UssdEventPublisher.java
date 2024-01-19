package com.quantumforge.quickdial.event;

import com.quantumforge.quickdial.context.UssdExecutable;
import com.quantumforge.quickdial.messaging.template.strut.MessageDocuments;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UssdEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;
    private static ApplicationEventPublisher sApplicationEventPublisher;

    @Bean
    public void ussdEventPublisherConfiguration(){
        sApplicationEventPublisher = applicationEventPublisher;
    }

    public void publishUssdEvent(UssdApplicationEvent event){
        applicationEventPublisher.publishEvent(event);
    }
    public static void staticPublishEvent(UssdApplicationEvent event){
        sApplicationEventPublisher.publishEvent(event);
    }


    // SESSION
    public static void publishSessionInitEvent(UssdSession session, SessionInitData sessionInitData){
        UssdUserSessionInitializedEvent sessionInitializedEvent = new UssdUserSessionInitializedEvent(session, sessionInitData);
        staticPublishEvent(sessionInitializedEvent);
    }

    public static void publishSessionUpdatedEvent(UssdSession session, SessionInitData sessionInitData){
        UssdUserSessionUpdatedEvent sessionUpdatedEvent = new UssdUserSessionUpdatedEvent(session, sessionInitData);
        staticPublishEvent(sessionUpdatedEvent);
    }

    public static void publishSessionPreDestroyedEvent(UssdSession session){
        UssdUserSessionPreDestroyedEvent sessionPreDestroyedEvent = new UssdUserSessionPreDestroyedEvent(session);
        staticPublishEvent(sessionPreDestroyedEvent);
    }

    public static void publishSessionPostDestroyedEvent(){
        UssdUserSessionPostDestroyedEvent sessionPostDestroyedEvent = new UssdUserSessionPostDestroyedEvent();
        staticPublishEvent(sessionPostDestroyedEvent);
    }

    // MESSAGE DOCUMENTS
    public static void publishMessageDocumentInitializedEvent(MessageDocuments messageDocuments){
        UssdMessageDocumentContainerInitializedEvent documentContainerInitializedEvent = new UssdMessageDocumentContainerInitializedEvent(messageDocuments);
        staticPublishEvent(documentContainerInitializedEvent);
    }

    // USSD-MAPPING
    public static void publishUssdMappingExecutionContextInitializedEvent(List<UssdExecutable> executables){
        UssdMappingExecutionContextInitializedEvent contextInitializedEvent = new UssdMappingExecutionContextInitializedEvent(executables);
        staticPublishEvent(contextInitializedEvent);
    }
}
