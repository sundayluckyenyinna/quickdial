package com.quantumforge.quickdial.annotation;

import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.execution.provider.UssdInvocationType;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

@Documented
@Configuration
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UssdMenuHandler {
    String prefix() default StringValues.EMPTY_STRING;
    String menu() default StringValues.EMPTY_STRING;
    UssdInvocationType type() default UssdInvocationType.PROGRESSIVE;
}
