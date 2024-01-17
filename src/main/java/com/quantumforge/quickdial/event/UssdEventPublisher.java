package com.quantumforge.quickdial.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UssdEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;
}
