package com.quantumforge.quickdial.messaging.starter;

import com.quantumforge.quickdial.bank.global.ApplicationItem;
import com.quantumforge.quickdial.bank.global.ApplicationStore;
import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.exception.InvalidUssdMessageSourceException;
import com.quantumforge.quickdial.exception.UnsupportedUssdMessageDocumentSourceException;
import com.quantumforge.quickdial.messaging.builder.DocumentType;
import com.quantumforge.quickdial.messaging.builder.MessageSourceDocumentBuilder;
import com.quantumforge.quickdial.messaging.config.QuickDialMessageSourceConfigDefaultProperties;
import com.quantumforge.quickdial.messaging.config.QuickDialMessageSourceConfigurationProperties;
import com.quantumforge.quickdial.messaging.template.NestedFileSeparator;
import com.quantumforge.quickdial.messaging.template.engine.DefaultUssdMessageDocumentResolver;
import com.quantumforge.quickdial.messaging.template.engine.UssdMessageDocumentResolver;
import com.quantumforge.quickdial.messaging.template.strut.FileResource;
import com.quantumforge.quickdial.messaging.template.strut.MessageDocument;
import com.quantumforge.quickdial.messaging.template.strut.MessageDocuments;
import com.quantumforge.quickdial.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = QuickDialMessageSourceConfigurationProperties.class)
public class QuickDialMessageSourceStarter{

    private final ApplicationStore applicationStore;
    private final ConfigurableApplicationContext applicationContext;
    private final List<MessageSourceDocumentBuilder> documentRegistries;
    private final QuickDialMessageSourceConfigurationProperties properties;
    public static QuickDialMessageSourceConfigurationProperties sProperties;

    @Bean
    public String messageSourceInitMemory(){
        initMessageSourceOnStartup();
        sProperties = properties;
        return StringValues.BEAN_CREATION_SUCCESS;
    }

    private void initMessageSourceOnStartup(){
        try {
            Resource resource = new ClassPathResource(properties.getTemplatePath());
            File file = resource.getFile();
            if(!file.exists()){
                log.error("Could not find default nor any configured folder for quickdial ussd messages!");
            }
            else if (!file.isDirectory()){
                String message = "The default or configured quickdial message source path must be a folder and not a file!";
                log.error(message);
                throw new InvalidUssdMessageSourceException(message);
            }
            saveMessageDocumentsToMemory(FileUtils.getFileResourcesInBaseFolder(file, resolveNestedFileSeparator(properties.getNestedFileSeparator())));
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    private void saveMessageDocumentsToMemory(List<FileResource> fileResources) {
        MessageDocuments messageDocuments = new MessageDocuments();
        fileResources
                .forEach(fileResource -> {
                    MessageSourceDocumentBuilder registry = getSupportingDocumentRegistry(fileResource.getFileExtension());
                    MessageDocument messageDocument = registry.buildDocument(fileResource);
                    messageDocuments.getMessageDocuments().add(messageDocument);
                });
        applicationStore.setItem(ApplicationItem.MESSAGE_DOCUMENTS.name(), messageDocuments);
        registerMessageDocumentToConfigurableApplicationContext(messageDocuments);
        if(properties.isEnableVerboseTemplateLogging()){
            verboseMessageDocumentStarterLog(messageDocuments);
        }
    }

    private void registerMessageDocumentToConfigurableApplicationContext(MessageDocuments messageDocuments){
        messageDocuments.getMessageDocuments().forEach(messageDocument -> {
            String beanName = messageDocument.getQualifiedName();
            UssdMessageDocumentResolver documentResolver = new DefaultUssdMessageDocumentResolver(messageDocument);
            applicationContext.getBeanFactory().registerSingleton(beanName, documentResolver);
        });
    }

    private MessageSourceDocumentBuilder getSupportingDocumentRegistry(DocumentType documentType){
        return documentRegistries.stream()
                .filter(messageSourceDocumentBuilder -> messageSourceDocumentBuilder.supportsDocumentType(documentType))
                .findFirst()
                .orElseThrow(() -> new UnsupportedUssdMessageDocumentSourceException(getUnsupportedMessageDocumentExceptionMessage(documentType)));
    }

    private MessageSourceDocumentBuilder getSupportingDocumentRegistry(String extension){
        DocumentType documentType = documentRegistries.get(0).getDocumentTypeByExtension(extension);
        return getSupportingDocumentRegistry(documentType);
    }

    private String getUnsupportedMessageDocumentExceptionMessage(DocumentType documentType){
        return String.format("Unsupported document file format for type %s", documentType);
    }

    private String resolveNestedFileSeparator(String separator){
        try{
            return NestedFileSeparator.valueOf(separator.toUpperCase()).getValue();
        }catch (Exception exception){
            return separator;
        }
    }

    private static void verboseMessageDocumentStarterLog(MessageDocuments messageDocuments){
        log.info("================================================= USSD MESSAGE DOCUMENTS =================================================");
        messageDocuments.getMessageDocuments().forEach(messageDocument -> {
            boolean isLastMessage = messageDocuments.getMessageDocuments().indexOf(messageDocument) == messageDocuments.getMessageDocuments().size() - 1;
            log.info("File name: {}", messageDocument.getFileName());
            log.info("Qualified file name: {}", messageDocument.getQualifiedName());
            log.info("Number of scanned messages: {}", messageDocument.getMessages().size());
            try{
                log.info("Absolute file path: {}", messageDocument.getFile().getAbsolutePath());
            }catch (Exception ignored){}
            if(!isLastMessage){
                log.info("--------------------------------------------------------------------------------------------------------------------------");
            }
        });
        log.info("==========================================================================================================================");
    }
}
