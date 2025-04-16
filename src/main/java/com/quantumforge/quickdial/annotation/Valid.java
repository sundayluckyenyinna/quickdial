package com.quantumforge.quickdial.annotation;

import com.quantumforge.quickdial.common.StringValues;

import java.lang.annotation.*;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Valid {
    String pattern() default StringValues.EMPTY_STRING;
    String message() default "Invalid input";
}
