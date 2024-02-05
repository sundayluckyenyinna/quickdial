package com.quantumforge.quickdial.interceptor;

import com.quantumforge.quickdial.interceptor.impl.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfiguration
@Import(value = {
      DefaultOptionCheckInterceptor.class, DefaultUssdInputInterceptorExecution.class,
      DefaultUssdRegistrationInterceptor.class, DefaultUssdSessionTerminationInterceptor.class,
      UssdGoBackSpecialInputInterceptor.class, UssdGoForwardSpecialInputInterceptor.class
})
public class InterceptorModule {
}
