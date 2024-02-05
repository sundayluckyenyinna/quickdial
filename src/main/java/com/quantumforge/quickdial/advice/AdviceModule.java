package com.quantumforge.quickdial.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@Configuration
@AutoConfiguration
@Import(value = { DefaultUssdExceptionAdvice.class })
public class AdviceModule {
}
