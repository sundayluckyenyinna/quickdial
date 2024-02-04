package com.quantumforge.quickdial.stats.model;

import com.quantumforge.quickdial.context.UssdExecutableType;
import lombok.Data;

import java.lang.reflect.Method;

@Data
public class UssdExecutionContextModel {
    private String ussdMapping;
    private String invocableMethod;
    private String callableClassName;
    private String classBeanName;
    private boolean isPossessLock = false;
    private UssdExecutableType parentExecutionType;
    private String contextId;

}
