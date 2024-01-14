package com.quantumforge.quickdial.context.providers;

import com.quantumforge.quickdial.context.UssdExecutableType;
import com.quantumforge.quickdial.context.UssdExecutionContext;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SoleUssdContextDataProvider implements UssdContextDataProvider{

    @Override
    public boolean supports(UssdExecutableType executableType) {
        return executableType == UssdExecutableType.SOLE_EXECUTABLE;
    }

    @Override
    public String provide(String input,  UssdExecutionContext incomingContext, UssdSession session) {
        return null;
    }
}
