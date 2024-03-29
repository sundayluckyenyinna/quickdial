package com.quantumforge.quickdial.bootstrap;

import com.quantumforge.quickdial.annotation.UssdGroupMapping;
import com.quantumforge.quickdial.annotation.UssdMenuMapping;
import com.quantumforge.quickdial.annotation.UssdSubMenuMapping;
import com.quantumforge.quickdial.bank.transit.UssdMappingRegistry;
import com.quantumforge.quickdial.bank.transit.factory.UssdStringMappingConstructor;
import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.context.UssdExecutableType;
import com.quantumforge.quickdial.context.UssdExecutionContext;
import com.quantumforge.quickdial.execution.result.ClassToMethodReferenceResolverUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UssdMappingRegistryBootstrap {

    private final ApplicationContext applicationContext;
    private final UssdMappingRegistry ussdMappingRegistry;
    private final UssdStringMappingConstructor stringMappingConstructor;
    private final ClassToMethodReferenceResolverUtils referenceResolverUtils;


    @Bean
    public String initUssdMappingRegistration(){
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(UssdMenuMapping.class);
        for(Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
            String beanName = entry.getKey();
            Object callableObjectInstance = entry.getValue();
            Class<?> beanClass = ClassUtils.getUserClass(applicationContext.getBean(beanName).getClass());
            List<Method> subHandlerMethods = Arrays.stream(beanClass.getMethods())
                    .peek(method -> method.setAccessible(true))
                    .filter(method -> method.isAnnotationPresent(UssdSubMenuMapping.class))
                    .toList();

            subHandlerMethods.forEach(method -> {
                UssdMenuMapping menuHandler = method.getDeclaringClass().getAnnotation(UssdMenuMapping.class);
                UssdSubMenuMapping subMenuHandler = method.getAnnotation(UssdSubMenuMapping.class);
                String stringMapping = stringMappingConstructor.constructStringMapping(menuHandler, subMenuHandler);
                UssdExecutionContext executionContext = UssdExecutionContext.builder()
                        .ussdMapping(stringMapping)
                        .invocableMethod(method)
                        .callableClassObject(callableObjectInstance)
                        .callableClass(beanClass)
                        .callableClassName(beanClass.getName())
                        .classBeanName(beanName)
                        .groupMapping(method.getAnnotation(UssdGroupMapping.class))
                        .parentExecutionType(method.isAnnotationPresent(UssdGroupMapping.class) ? UssdExecutableType.GROUP_EXECUTABLE : UssdExecutableType.SOLE_EXECUTABLE)
                        .isPossessLock(false)
                        .menuHandler(menuHandler)
                        .ussdSubMenuMapping(subMenuHandler)
                        .build();
                executionContext.setContextId(referenceResolverUtils.resolveUssdContextId(executionContext));
                ussdMappingRegistry.registerUssdMapping(executionContext);
            });
        }
        return StringValues.BEAN_CREATION_SUCCESS;
    }
}
