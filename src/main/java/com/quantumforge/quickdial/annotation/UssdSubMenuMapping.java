package com.quantumforge.quickdial.annotation;

import com.quantumforge.quickdial.common.StringValues;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UssdSubMenuMapping {

    String id() default StringValues.EMPTY_STRING;
    String submenu() default StringValues.EMPTY_STRING;
    boolean relaxBackwardNavigation() default false;
    boolean relaxMenuOptionCheck() default false;
    boolean hideNavigationOptions() default false;
    boolean hideBackwardNavOption() default false;
    boolean hideForwardNavOption() default false;
    int navOptionTopPadding() default 1;
}
