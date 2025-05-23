package com.quantumforge.quickdial.annotation;

import com.quantumforge.quickdial.common.StringValues;

import java.lang.annotation.*;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface UssdParam {
    String value() default StringValues.EMPTY_STRING;
}
