package com.quantumforge.quickdial.bank.transit.factory;

import com.quantumforge.quickdial.bank.transit.UssdMapType;
import com.quantumforge.quickdial.context.UssdExecutionContext;
import com.quantumforge.quickdial.util.QuickDialUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ParamUssdMapTypeContextProvider implements UssdMappingContextProvider{

    private final QuickDialUtil quickDialUtil;

    @Override
    public UssdExecutionContext getMatchingContext(String mapping, List<UssdExecutionContext> contexts) {
        return getParameterizedMatchingContext(mapping, contexts, quickDialUtil);
    }

    @Override
    public boolean supportUssdMapType(UssdMapType mapType) {
        return mapType == UssdMapType.PARAM_MAPPING;
    }
}
