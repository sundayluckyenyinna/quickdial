package com.quantumforge.quickdial.bootstrap;

import com.quantumforge.quickdial.annotation.UssdGroupMapping;
import com.quantumforge.quickdial.annotation.UssdMenuHandler;
import com.quantumforge.quickdial.annotation.UssdSubMenuHandler;
import com.quantumforge.quickdial.bank.transit.UssdMappingRegistry;
import com.quantumforge.quickdial.bank.transit.factory.UssdStringMappingConstructor;
import com.quantumforge.quickdial.context.UssdExecutableType;
import com.quantumforge.quickdial.context.UssdExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UssdMappingRegistryBootstrap {

    private final ApplicationContext applicationContext;
    private final UssdMappingRegistry ussdMappingRegistry;
    private final UssdStringMappingConstructor stringMappingConstructor;

    @Bean
    public void initUssdMappingRegistration(){
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(UssdMenuHandler.class);
        for(Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
            String beanName = entry.getKey();
            Object callableObjectInstance = entry.getValue();
            Class<?> beanClass = applicationContext.getBean(beanName).getClass();

            List<Method> subHandlerMethods = Arrays.stream(beanClass.getMethods())
                    .peek(method -> method.setAccessible(true))
                    .filter(method -> method.isAnnotationPresent(UssdSubMenuHandler.class))
                    .collect(Collectors.toList());

            subHandlerMethods.forEach(method -> {
                UssdMenuHandler menuHandler = method.getDeclaringClass().getAnnotation(UssdMenuHandler.class);
                UssdSubMenuHandler subMenuHandler = method.getAnnotation(UssdSubMenuHandler.class);
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
                        .build();
                ussdMappingRegistry.registerUssdMapping(executionContext);
            });
        }
    }
}
