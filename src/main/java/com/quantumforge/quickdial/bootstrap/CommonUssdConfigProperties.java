package com.quantumforge.quickdial.bootstrap;

import com.quantumforge.quickdial.util.QuickDialUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.ussd.properties.config")
public class CommonUssdConfigProperties {

    private String baseUssdCode = "*123#";
    private String goBackOption = "0";
    private String goForwardOption = "00";
    private boolean enableMenuOptionCheck = true;
    private Integer acceptableInputTrialTimes = 3;
    private boolean enableVerboseMappingLogs = true;
    private String goBackMenuText = "Go back";
    private String goForwardMenuText = "Go forward";

    public String getBaseUssdCodeWithoutEndDelimiter(){
        int lastIndexOfEndDelimiter = baseUssdCode.lastIndexOf(QuickDialUtil.sProperties.getEndDelimiter());
        if(lastIndexOfEndDelimiter != -1){
            return this.getBaseUssdCode().substring(0, lastIndexOfEndDelimiter);
        }
        return this.getBaseUssdCode();
    }
}
