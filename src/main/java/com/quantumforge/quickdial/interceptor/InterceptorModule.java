package com.quantumforge.quickdial.interceptor;

import com.quantumforge.quickdial.interceptor.impl.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfiguration
@Import(value = {
        DefaultOptionCheckInterceptor.class,
        DefaultUssdInputInterceptorExecution.class,
        DefaultUssdParamPatternCheckInterceptor.class,
        DefaultUssdRegistrationInterceptor.class,
        UssdGoBackSpecialInputInterceptor.class,
        UssdGoForwardSpecialInputInterceptor.class
})
public class InterceptorModule {
}
