package com.quantumforge.quickdial.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = CommonUssdConfigProperties.class)
public class CommonBeanProviderBootstrap {

    private final CommonUssdConfigProperties configProperties;

}
