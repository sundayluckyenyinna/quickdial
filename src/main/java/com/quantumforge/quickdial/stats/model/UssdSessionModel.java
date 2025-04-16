package com.quantumforge.quickdial.stats.model;

import com.quantumforge.quickdial.session.BackwardNavigableList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UssdSessionModel {
    private String sessionId;
    private BackwardNavigableList<UssdUserExecutionContextModel> executionContextChain = new BackwardNavigableList<>();
    private Map<Object, Object> sessionData = new LinkedHashMap<>();
    private Map<String, Object> ussdModel = new LinkedHashMap<>();
    private String createdAt;
    private boolean isFresh;
}
