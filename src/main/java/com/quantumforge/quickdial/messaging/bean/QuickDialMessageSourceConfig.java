package com.quantumforge.quickdial.messaging.bean;

import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.exception.InvalidUssdMessageSourceException;
import com.quantumforge.quickdial.messaging.config.QuickDialMessageSourceConfigurationProperties;
import com.quantumforge.quickdial.util.GeneralUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuickDialMessageSourceConfig {

    private final QuickDialMessageSourceConfigurationProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public QuickDialMessageResource defaultQuickDialMessageResource() throws IOException {
        if(!GeneralUtils.isNullOrEmpty(properties.getTemplatePath())){
            return QuickDialMessageResource.builder()
                    .primaryResourceFolder(getClassPathUssdMessageDocumentFolder())
                    .name(StringValues.EMPTY_STRING)
                    .build();
        }
        return new QuickDialMessageResource();
    }

    private File getClassPathUssdMessageDocumentFolder() throws IOException {
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
        return file;
    }

}
