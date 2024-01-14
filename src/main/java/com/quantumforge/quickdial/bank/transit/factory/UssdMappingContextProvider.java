package com.quantumforge.quickdial.bank.transit.factory;

import com.quantumforge.quickdial.bank.transit.UssdMapType;
import com.quantumforge.quickdial.context.UssdExecutionContext;
import com.quantumforge.quickdial.util.QuickDialUtil;

import java.util.List;
import java.util.Objects;

public interface UssdMappingContextProvider {

    UssdExecutionContext getMatchingContext(String mapping, List<UssdExecutionContext> contexts);

    boolean supportUssdMapType(UssdMapType mapType);

    default UssdExecutionContext getParameterizedMatchingContext(String mapping, List<UssdExecutionContext> contexts, QuickDialUtil quickDialUtil){
        List<String> mappingTokens = quickDialUtil.getTokensBetweenDelimiters(mapping);
        UssdExecutionContext firstMatchingExecutionContext = null;
        for(UssdExecutionContext context : contexts){
            String registeredUssdMapping = context.getUssdMapping();
            if(Objects.nonNull(registeredUssdMapping) && !registeredUssdMapping.trim().isEmpty()){
                List<String> registeredMappingTokens = quickDialUtil.getTokensBetweenDelimiters(registeredUssdMapping);
                boolean matchFound = true;
                if(mappingTokens.size() == registeredMappingTokens.size()){
                    for(int i = 0; i < mappingTokens.size(); i++){
                        String registeredToken = registeredMappingTokens.get(i);
                        if(QuickDialUtil.isParamPlaceHolder(registeredToken)){
                            continue;
                        }
                        String token = mappingTokens.get(i);
                        matchFound = token.equalsIgnoreCase(registeredToken);
                        if(!matchFound){
                            break;              // break once a mismatch is discovered.
                        }
                    }
                    if(matchFound){
                        firstMatchingExecutionContext = context;
                        break;                  // break once a match is found.
                    }
                }
            }
        }
        return firstMatchingExecutionContext;
    }
}
