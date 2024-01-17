package com.quantumforge.quickdial.test;

import com.quantumforge.quickdial.bank.transit.UssdUserSessionRegistry;
import com.quantumforge.quickdial.context.UssdUserExecutionContext;
import com.quantumforge.quickdial.execution.QuickDialUssdExecutor;
import com.quantumforge.quickdial.payload.QuickDialPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final UssdUserSessionRegistry registry;
    private final QuickDialUssdExecutor ussdExecutor;

    @GetMapping("/ussd")
    public ResponseEntity<?> testGet(){
        return ResponseEntity.ok("See now");
    }

    @PostMapping(value = "/test-ussd")
    public ResponseEntity<?> testPost(@RequestBody QuickDialPayload payload){
        return ResponseEntity.ok(ussdExecutor.submit(payload));
    }

    @GetMapping("/test-ussd/{sessionId}")
    public ResponseEntity<List<String>> displayCustomerUssdSession(@PathVariable String sessionId){
        return ResponseEntity.ok(registry.getSession(sessionId).getExecutionContextChain().stream().map(UssdUserExecutionContext::getUssdCode).collect(Collectors.toList()));
    }
}
