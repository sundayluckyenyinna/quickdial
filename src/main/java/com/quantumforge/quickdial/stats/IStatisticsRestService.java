package com.quantumforge.quickdial.stats;

import java.util.Map;

public interface IStatisticsRestService{
    Map<String, Object> getUssdMessageDocuments();

    Map<String, Object> getAllConfiguredUssdExecutionContextMappings();

    Map<String, Object> getAllUssdUserExecutionContextBySessionId(String sessionId);

    Map<String, Object> getAllCurrentRunningSessions();
}
