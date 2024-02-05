package com.quantumforge.quickdial.messaging.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "quick-dial.messages.template")
public class QuickDialMessageTemplateEngineConfigProperties {
    private String preferredEngine = "thymeleaf";
}
