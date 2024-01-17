package com.quantumforge.quickdial.session;

import com.quantumforge.quickdial.context.UssdExecutableType;
import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.exception.RedirectUssdPageNotFoundException;
import com.quantumforge.quickdial.util.GeneralUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UssdSession {

    /**
     * The current sessionId that uniquely identifies the USSD user. In most cases, the sessionId is a unique UUID string.
     * This is guaranteed not to conflict for other users.
     */
    private String sessionId;


    /**
     * The complete stack of the every execution between the Ussd user and the ussd application framework. This stack works in the
     * usual way of this data structure such that the latest execution is the first to be referenced.
     */
    private BackwardNavigableList<UssdUserExecutionContext> executionContextChain = new BackwardNavigableList<>();

    private SessionData sessionData = new SessionData(this);

    private UssdModel ussdModel = new UssdModel(this);

    public UssdUserExecutionContext getLatestUssdUserExecutionContextInGroup(String groupId){
        return executionContextChain.stream()
                .filter(ussdUserExecutionContext -> ussdUserExecutionContext.getExecutionContext().getParentExecutionType() == UssdExecutableType.GROUP_EXECUTABLE)
                .filter(ussdUserExecutionContext -> Objects.nonNull(ussdUserExecutionContext.getExecutionContext().getGroupMapping()))
                .filter(ussdUserExecutionContext -> ussdUserExecutionContext.getExecutionContext().getGroupMapping().id().equalsIgnoreCase(groupId))
                .reduce((a, b) -> b)
                .orElse(null);
    }

    public void updateUserUssdNavigationContext(UssdUserExecutionContext ussdUserExecutionContext){
        UssdUserExecutionContext matchingUssdUserExecutionContext = this.getExecutionContextChain()
                .stream()
                .filter(context -> context.getExecutionContext().sameAs(ussdUserExecutionContext.getExecutionContext()))
                .findFirst()
                .orElse(null);
        if(Objects.nonNull(matchingUssdUserExecutionContext) && matchingUssdUserExecutionContext.isSole()){
            int index = this.getExecutionContextChain().indexOf(matchingUssdUserExecutionContext);
            this.getExecutionContextChain().set(index, ussdUserExecutionContext);
        }
        else if(Objects.nonNull(matchingUssdUserExecutionContext) && matchingUssdUserExecutionContext.isInGroup()){
            UssdUserExecutionContext lastMatched = getLatestUssdUserExecutionContextInGroup(matchingUssdUserExecutionContext.getExecutionContext().getGroupMapping().id());
            int index = this.getExecutionContextChain().indexOf(lastMatched);
            if(hasNotRegisteredUserUssdContext(ussdUserExecutionContext)) {
                this.getExecutionContextChain().add(index + 1, ussdUserExecutionContext);
            }
            if(getExecutionContextChain().hasNext()){
                getExecutionContextChain().moveCurrentIndexForward();
            }
        }
        if(hasNotRegisteredUserUssdContext(ussdUserExecutionContext)){
            this.getExecutionContextChain().add(ussdUserExecutionContext);
        }

    }

    public boolean hasNotRegisteredUserUssdContext(UssdUserExecutionContext context){
        return this.getExecutionContextChain()
                .stream()
                .noneMatch(ussdContext -> ussdContext.getUssdCode().equalsIgnoreCase(context.getUssdCode()) &&
                        ussdContext.getExecutionContext().getInvocableMethod().equals(context.getExecutionContext().getInvocableMethod()) &&
                        ussdContext.getExecutionContext().getCallableClass().equals(context.getExecutionContext().getCallableClass()));
    }

    public UssdUserExecutionContext getUssdUserExecutionContextByContextId(String contextId){
        return this.getExecutionContextChain().stream()
                .filter(ussdUserExecutionContext -> ussdUserExecutionContext.getExecutionContext().getContextId().equalsIgnoreCase(contextId))
                .findFirst()
                .orElseThrow(() -> new RedirectUssdPageNotFoundException(String.format("No redirect ussd page or context with contextId = '%s' currently found for user execution context", contextId)));
    }

    public void setFocusOnContext(UssdUserExecutionContext ussdUserExecutionContext) {
        if (!GeneralUtils.isNullOrEmpty(ussdUserExecutionContext)) {
            boolean isFocused = this.getExecutionContextChain().focus(ussdUserExecutionContext);
            if (isFocused) {
                int currentIndex = this.getExecutionContextChain().getCurrentNavigationIndex();
                BackwardNavigableList<UssdUserExecutionContext> newContext = new BackwardNavigableList<>();
                List<UssdUserExecutionContext> listContext = this.getExecutionContextChain().subList(0, currentIndex + 1);
                newContext.addAll(listContext);
                newContext.setCurrentNavigationIndexAtIndex(currentIndex);
                this.setExecutionContextChain(newContext);
            }
        }
    }


    public boolean isAfter(UssdUserExecutionContext firstContext, UssdUserExecutionContext secondContext){
        int thisIndex = this.getExecutionContextChain().indexOf(firstContext);
        if(thisIndex == -1){ return false; }
        int secondContextIndex = this.getExecutionContextChain().indexOf(secondContext);
        if(secondContextIndex == -1){ return false; }
        return thisIndex > secondContextIndex;
    }

    public boolean isBefore(UssdUserExecutionContext firstContext, UssdUserExecutionContext secondContext){
        int thisIndex = this.getExecutionContextChain().indexOf(firstContext);
        if(thisIndex == -1){ return false; }
        int secondContextIndex = this.getExecutionContextChain().indexOf(secondContext);
        if(secondContextIndex == -1){ return false; }
        return thisIndex < secondContextIndex;
    }
}
