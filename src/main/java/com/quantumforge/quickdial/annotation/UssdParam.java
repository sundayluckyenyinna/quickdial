package com.quantumforge.quickdial.annotation;

import com.quantumforge.quickdial.common.StringValues;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface UssdParam {
    String value() default StringValues.EMPTY_STRING;
}
