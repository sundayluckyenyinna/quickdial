package com.quantumforge.quickdial.context;

import com.quantumforge.quickdial.annotation.UssdGroupMapping;
import com.quantumforge.quickdial.annotation.UssdMenuMapping;
import com.quantumforge.quickdial.annotation.UssdSubMenuMapping;
import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.util.QuickDialUtil;
import lombok.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UssdExecutionContext {
    private String ussdMapping;
    private Method invocableMethod;
    private Object callableClassObject;
    private Class<?> callableClass;
    private String callableClassName;
    private String classBeanName;
    private boolean isPossessLock = false;
    private UssdMenuMapping menuHandler;
    private UssdSubMenuMapping ussdSubMenuMapping;
    private UssdGroupMapping groupMapping;
    private UssdExecutableType parentExecutionType;
    private String contextId;

    public boolean sameAs(UssdExecutionContext ussdExecutionContext){
        if(ussdExecutionContext.getParentExecutionType() == this.getParentExecutionType()){
            if(this.getParentExecutionType() == UssdExecutableType.SOLE_EXECUTABLE){
                return sameAsInSoleExecutable(ussdExecutionContext);
            }
            else if(this.getParentExecutionType() == UssdExecutableType.GROUP_EXECUTABLE){
                return sameAsInGroupExecutable(ussdExecutionContext);
            }
            return false;
        }
        return false;
    }

    private boolean sameAsInSoleExecutable(UssdExecutionContext ussdExecutionContext){
        final String parameterPlaceHolder = "{parameter}";
        String normalizedMapping = this.getUssdMapping().replaceAll(StringValues.SIMPLE_PARAMETERIZED_PATTERN, parameterPlaceHolder);
        String incomingNormalizedMapping = ussdExecutionContext.getUssdMapping().replaceAll(StringValues.SIMPLE_PARAMETERIZED_PATTERN, parameterPlaceHolder);
        boolean exactEquality = normalizedMapping.equalsIgnoreCase(incomingNormalizedMapping);
        List<String> tokensInNormalizedMapping = QuickDialUtil.getStaticTokensBetweenDelimiters(normalizedMapping);
        List<String> tokensInIncoming = QuickDialUtil.getStaticTokensBetweenDelimiters(incomingNormalizedMapping);
        boolean sizeEquality = tokensInNormalizedMapping.size() == tokensInIncoming.size();
        return exactEquality || sizeEquality;
    }

    private boolean sameAsInGroupExecutable(UssdExecutionContext ussdExecutionContext){
        if(Objects.nonNull(this.getGroupMapping()) && Objects.nonNull(ussdExecutionContext.getGroupMapping())){
            if(!this.getGroupMapping().id().equalsIgnoreCase(ussdExecutionContext.getGroupMapping().id())){
                return sameAsInSoleExecutable(ussdExecutionContext);
            }
            return true;  // they are in same group, so they are same by virtue of the same ussd code mapping.
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ussdMapping, invocableMethod, callableClassObject, callableClass, callableClassName, classBeanName, isPossessLock, groupMapping, parentExecutionType);
    }
}
