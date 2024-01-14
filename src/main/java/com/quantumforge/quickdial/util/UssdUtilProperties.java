package com.quantumforge.quickdial.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ussd.util")
public class UssdUtilProperties {

    private String startDelimiter = "\\*";
    private String endDelimiter = "#";
    private String shortCodePrefix = "SCM";
}
