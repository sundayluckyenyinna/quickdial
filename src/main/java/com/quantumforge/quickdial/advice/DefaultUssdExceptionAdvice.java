package com.quantumforge.quickdial.advice;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quantumforge.quickdial.exception.NoUssdMappingFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DefaultUssdExceptionAdvice {

    private static final String STATUS_KEY = "status";

    @ExceptionHandler(value = NoUssdMappingFoundException.class)
    public void handleNoUssdMappingFoundException(NoUssdMappingFoundException exception, HttpServletResponse servletResponse){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("timestamp", LocalDateTime.now().toString());
        responseMap.put(STATUS_KEY, 400);
        responseMap.put("error", "Ussd mapping error");
        responseMap.put("message", exception.getMessage());
        responseMap.put("ussd-code", exception.getUssdCode());
        pushExceptionResponse(responseMap, servletResponse);
    }

    @SneakyThrows
    private void pushExceptionResponse(Map<String, Object> responseMap, HttpServletResponse servletResponse){
        String responseJson = getObjectMapper().writeValueAsString(responseMap);
        int status = HttpStatus.BAD_REQUEST.value();
        Object statusObject = responseMap.get(STATUS_KEY);
        if(Objects.nonNull(statusObject)){
            status = Integer.parseInt(String.valueOf(statusObject));
        }
        servletResponse.setHeader("X-FORWARDED-FOR", "quantumforge");
        servletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        servletResponse.setStatus(status);
        servletResponse.getWriter().write(responseJson);
    }

    private static ObjectMapper getObjectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        return objectMapper;
    }
}
