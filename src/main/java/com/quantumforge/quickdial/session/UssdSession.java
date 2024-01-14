package com.quantumforge.quickdial.session;

import com.quantumforge.quickdial.context.UssdExecutableType;
import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.messaging.template.strut.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.stream.Collectors;


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
    private BackwardNavigableList<UssdUserExecutionContext> executionContext = new BackwardNavigableList<>();

    private SessionData sessionData = new SessionData(this);

    private UssdModel ussdModel = new UssdModel(this);

    private Message latestMessage;


    public UssdUserExecutionContext getLatestUssdUserExecutionContextInGroup(String groupId){
        return executionContext.stream()
                .filter(ussdUserExecutionContext -> ussdUserExecutionContext.getExecutionContext().getParentExecutionType() == UssdExecutableType.GROUP_EXECUTABLE)
                .filter(ussdUserExecutionContext -> Objects.nonNull(ussdUserExecutionContext.getExecutionContext().getGroupMapping()))
                .filter(ussdUserExecutionContext -> ussdUserExecutionContext.getExecutionContext().getGroupMapping().id().equalsIgnoreCase(groupId))
                .reduce((a, b) -> b)
                .orElse(null);
    }

    public void updateUserUssdNavigationContext(UssdUserExecutionContext ussdUserExecutionContext){
        UssdUserExecutionContext matchingUssdUserExecutionContext = this.getExecutionContext()
                .stream()
                .filter(context -> context.getExecutionContext().sameAs(ussdUserExecutionContext.getExecutionContext()))
                .findFirst()
                .orElse(null);
        if(Objects.nonNull(matchingUssdUserExecutionContext) && matchingUssdUserExecutionContext.isSole()){
            int index = this.getExecutionContext().indexOf(matchingUssdUserExecutionContext);
            this.getExecutionContext().set(index, ussdUserExecutionContext);
        }
        else if(Objects.nonNull(matchingUssdUserExecutionContext) && matchingUssdUserExecutionContext.isInGroup()){
            UssdUserExecutionContext lastMatched = getLatestUssdUserExecutionContextInGroup(matchingUssdUserExecutionContext.getExecutionContext().getGroupMapping().id());
            int index = this.getExecutionContext().indexOf(lastMatched);
            if(hasNotRegisteredUserUssdContext(ussdUserExecutionContext)) {
                this.getExecutionContext().add(index + 1, ussdUserExecutionContext);
            }
            if(getExecutionContext().hasNext()){
                getExecutionContext().moveCurrentIndexForward();
            }
        }
        if(hasNotRegisteredUserUssdContext(ussdUserExecutionContext)){
            this.getExecutionContext().add(ussdUserExecutionContext);
        }
    }

    public boolean hasNotRegisteredUserUssdContext(UssdUserExecutionContext context){
        return this.getExecutionContext()
                .stream()
                .noneMatch(ussdContext -> ussdContext.getUssdCode().equalsIgnoreCase(context.getUssdCode()) &&
                        ussdContext.getExecutionContext().getInvocableMethod().equals(context.getExecutionContext().getInvocableMethod()) &&
                        ussdContext.getExecutionContext().getCallableClass().equals(context.getExecutionContext().getCallableClass()));
    }

    public void flushAllSessionContextButAtIndex(int index){
        UssdUserExecutionContext context = this.getExecutionContext().get(0);
        BackwardNavigableList<UssdUserExecutionContext> newExecutionContexts = new BackwardNavigableList<>();
        newExecutionContexts.add(context);
        this.setExecutionContext(newExecutionContexts);
    }

}
