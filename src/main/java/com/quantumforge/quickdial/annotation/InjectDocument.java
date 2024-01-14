package com.quantumforge.quickdial.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Lazy
@Autowired
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier
public @interface InjectDocument {
    @AliasFor(annotation = Qualifier.class, attribute = "value")
    String value();
}
