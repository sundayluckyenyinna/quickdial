package com.quantumforge.quickdial.context.providers;

import com.quantumforge.quickdial.bootstrap.CommonUssdConfigProperties;
import com.quantumforge.quickdial.context.UssdExecutableType;
import com.quantumforge.quickdial.context.UssdExecutionContext;
import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.session.UssdSession;
import com.quantumforge.quickdial.util.QuickDialUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GroupUssdContextDataProvider implements UssdContextDataProvider{

    private final QuickDialUtil quickDialUtil;
    private final CommonUssdConfigProperties ussdConfigProperties;

    @Override
    public boolean supports(UssdExecutableType executableType) {
        return executableType == UssdExecutableType.GROUP_EXECUTABLE;
    }

    /**
     * This is used to provide context data when the incoming context is a group
     * @param input
     * @param incomingContext
     * @param session
     * @return
     */
    @Override
    public String provide(String input, UssdExecutionContext incomingContext, UssdSession session) {
        UssdUserExecutionContext currentUserContext = session.getExecutionContext().getCurrentElement();
        if(input.trim().equalsIgnoreCase(ussdConfigProperties.getGoForwardOption())){
            if(session.getExecutionContext().hasNext()){
                return session.getExecutionContext().getNextElement().getContextData();
            }else{
                return currentUserContext.getContextData();
            }
        }
        // In this case, the user is not trying to go forward in a group, but to the next page of ussd execution
        else{
            if(currentUserContext.isInGroup()){
                return currentUserContext.getContextData();
            }else{
                return quickDialUtil.applicationChain(currentUserContext.getContextData(), input);
            }
        }
    }
}
