package com.quantumforge.quickdial.annotation;

import com.quantumforge.quickdial.common.StringValues;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UssdSubMenuHandler {

    String submenu() default StringValues.EMPTY_STRING;
    boolean relaxBackwardNavigation() default false;
}
