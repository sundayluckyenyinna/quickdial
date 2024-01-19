package com.quantumforge.quickdial.event;

import com.quantumforge.quickdial.context.UssdExecutable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Getter
public class UssdMappingExecutionContextInitializedEvent extends UssdApplicationEvent {

    public UssdMappingExecutionContextInitializedEvent(List<UssdExecutable> source) {
        super(source);
    }

}
