package com.quantumforge.quickdial.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class GroupUssdExecutableContextWrapper implements UssdExecutable{

    private String groupId;
    private String commonUssdMapping;
    private List<UssdExecutionContext> ussdExecutionContexts = new ArrayList<>();

    public GroupUssdExecutableContextWrapper() {
    }

    public UssdExecutionContext getFirst(){
        return ussdExecutionContexts.isEmpty() ? null : ussdExecutionContexts.get(0);
    }

    public UssdExecutionContext getLast(){
        return ussdExecutionContexts.isEmpty() ? null : ussdExecutionContexts.get(ussdExecutionContexts.size() - 1);
    }

    public UssdExecutionContext getNextAfter(UssdExecutionContext context){
        int index = ussdExecutionContexts.indexOf(context);
        if(ussdExecutionContexts.isEmpty()){ return null; }
        if(index >= ussdExecutionContexts.size() - 1){
            return getLast();
        }
        return ussdExecutionContexts.get(index + 1);
    }

    public UssdExecutionContext getPreviousFrom(UssdExecutionContext context){
        int index = ussdExecutionContexts.indexOf(context);
        if(ussdExecutionContexts.isEmpty()){ return null; }
        if(index <= 0){
            return getFirst();
        }
        return ussdExecutionContexts.get(index - 1);
    }

    public UssdExecutionContext getAt(int index){
        try {
            return ussdExecutionContexts.isEmpty() ? null : ussdExecutionContexts.get(index);
        }catch (Exception exception){
            return null;
        }
    }

    public boolean isLastInGroup(UssdExecutionContext ussdExecutionContext){
        return ussdExecutionContext.equals(this.getLast());
    }

    @Override
    public boolean supportExecutableType(UssdExecutableType executableType) {
        return executableType == UssdExecutableType.GROUP_EXECUTABLE;
    }

    @Override
    public UssdExecutableType getExecutableType() {
        return UssdExecutableType.GROUP_EXECUTABLE;
    }

    @Override
    public int mappingLength() {
        return commonUssdMapping.length();
    }
}
