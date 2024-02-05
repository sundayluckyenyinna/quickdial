package com.quantumforge.quickdial.execution;

import com.quantumforge.quickdial.execution.provider.DefaultUssdUserExecutionContextParameterProvider;
import com.quantumforge.quickdial.execution.result.ClassToMethodReferenceResolverUtils;
import com.quantumforge.quickdial.execution.result.EndUssdExecutionResultResolver;
import com.quantumforge.quickdial.execution.result.RedirectUssdExecutionResultResolver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfiguration
@Import(value = {
        DefaultUssdUserExecutionContextParameterProvider.class,
        ClassToMethodReferenceResolverUtils.class,
        EndUssdExecutionResultResolver.class,
        RedirectUssdExecutionResultResolver.class,
        DefaultUssdExecutor.class,
        UssdExecutionReflectionInvocationUtils.class
})
public class ExecutionModule {
}
