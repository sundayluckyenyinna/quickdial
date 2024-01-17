package com.quantumforge.quickdial.execution.result;

import com.quantumforge.quickdial.common.StringValues;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ussd.redirect")
public class UssdRedirectConfigProperties {
    private String referenceFormat = "default_reference";
    private boolean enableAutomaticErrorRedirectionMessage = true;
    private String inputValidationErrorRedirectReference = StringValues.EMPTY_STRING;
    private String defaultInputValidationMessage = "Invalid input. Please select correct option. ${errorRetryAttemptLeft} ${errorRetrySuffix} left.";
}
