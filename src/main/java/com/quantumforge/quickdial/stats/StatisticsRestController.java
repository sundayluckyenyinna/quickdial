package com.quantumforge.quickdial.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/quickdial")
public class StatisticsRestController {

    private final IStatisticsRestService statisticsRestService;


    @GetMapping("/message-documents")
    public ResponseEntity<Map<String, Object>> getConfiguredUssdMessageDocuments(){
        return ResponseEntity.ok(statisticsRestService.getUssdMessageDocuments());
    }

    @GetMapping("/mappings")
    public ResponseEntity<Map<String, Object>> getConfiguredUssdExecutionContexts(){
        return ResponseEntity.ok(statisticsRestService.getAllConfiguredUssdExecutionContextMappings());
    }

    @GetMapping("/user/contexts/{sessionId}")
    public ResponseEntity<Map<String, Object>> getAllUserExecutionContextsBySessionId(@PathVariable("sessionId")String sessionId){
        return ResponseEntity.ok(statisticsRestService.getAllUssdUserExecutionContextBySessionId(sessionId));
    }

    @GetMapping("/contexts/sessions")
    public ResponseEntity<Map<String, Object>> getAllCurrentRunningSessions(){
        return ResponseEntity.ok(statisticsRestService.getAllCurrentRunningSessions());
    }
}
