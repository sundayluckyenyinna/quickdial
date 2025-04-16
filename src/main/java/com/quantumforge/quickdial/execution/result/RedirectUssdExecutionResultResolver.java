package com.quantumforge.quickdial.execution.result;

import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.execution.UssdExecutionReflectionInvocationUtils;
import com.quantumforge.quickdial.payload.UssdExecution;
import com.quantumforge.quickdial.session.UssdSession;
import com.quantumforge.quickdial.util.GeneralUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedirectUssdExecutionResultResolver implements UssdExecutionResultResolver{

    private final ClassToMethodReferenceResolverUtils referenceResolverUtils;

    @Override
    public UssdExecution<?> getResolvedUssdBody(UssdExecution<?> execution, UssdSession session) {
        String redirectUssdPageId = execution.getRedirectUssdPageId();
        redirectUssdPageId = referenceResolverUtils.resolveUssdContextIdFromRedirectionRule(redirectUssdPageId, execution.getCurrentCallableClass());
        UssdUserExecutionContext ussdUserExecutionContext = session.getUssdUserExecutionContextByContextId(redirectUssdPageId);
        session.setFocusOnContext(ussdUserExecutionContext);
        GeneralUtils.doIf(!GeneralUtils.isNullOrEmpty(execution.getRedirectUssdPageInput()), ()-> ussdUserExecutionContext.updateInputAndCorrespondingContextData(execution.getRedirectUssdPageInput()));
        return UssdExecutionReflectionInvocationUtils.invokeUssdExecutionForSession(ussdUserExecutionContext, session);
    }

    @Override
    public boolean supportsState(UssdExecution.MenuReturnState returnState) {
        return returnState == UssdExecution.MenuReturnState.REDIRECT;
    }
}
