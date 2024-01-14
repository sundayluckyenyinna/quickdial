package com.quantumforge.quickdial.bootstrap;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ussd.properties.config")
public class CommonUssdConfigProperties {

    private String baseUssdCode = "*123#";
    private String goBackOption = "0";
    private String goForwardOption = "00";
    private String basePackageToScan = "com.quantumforge.quickdial";
    private boolean enableMenuOptionCheck = true;
    private Integer acceptableInputTrialTimes = 3;
    private boolean enableVerboseMappingLogs = true;
}
