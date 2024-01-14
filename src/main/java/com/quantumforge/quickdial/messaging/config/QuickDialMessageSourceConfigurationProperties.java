package com.quantumforge.quickdial.messaging.config;

import com.quantumforge.quickdial.common.StringValues;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.quantumforge.quickdial.messaging.config.QuickDialMessageSourceConfigDefaultProperties.*;

@Data
@ConfigurationProperties(prefix = "quick-dial")
public class QuickDialMessageSourceConfigurationProperties {

    private String templatePath = DEFAULT_TEMPLATE_PATH;
    private boolean enableVerboseTemplateLogging = DEFAULT_VERBOSE_TEMPLATE_LOGGING;
    private String nestedFileSeparator = DEFAULT_NESTED_FILE_SEPARATOR;
    private String optionToMessageSeparator = StringValues.DOT_SPACE;
}
