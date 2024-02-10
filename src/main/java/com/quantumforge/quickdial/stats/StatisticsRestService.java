package com.quantumforge.quickdial.stats;

import com.quantumforge.quickdial.bank.global.ApplicationItem;
import com.quantumforge.quickdial.bank.global.ApplicationStore;
import com.quantumforge.quickdial.bank.transit.UssdUserSessionRegistry;
import com.quantumforge.quickdial.bank.transit.impl.SimpleUssdMappingRegistry;
import com.quantumforge.quickdial.context.*;
import com.quantumforge.quickdial.messaging.template.strut.MessageDocuments;
import com.quantumforge.quickdial.session.BackwardNavigableList;
import com.quantumforge.quickdial.session.UssdSession;
import com.quantumforge.quickdial.stats.model.UssdExecutionContextModel;
import com.quantumforge.quickdial.stats.model.UssdMessageDocumentModel;
import com.quantumforge.quickdial.stats.model.UssdSessionModel;
import com.quantumforge.quickdial.stats.model.UssdUserExecutionContextModel;
import com.quantumforge.quickdial.util.GeneralUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsRestService implements IStatisticsRestService{

    private static final String DATA_KEY = "data";
    private static final String MESSAGE_KEY = "message";
    private static final String STATUS_KEY = "status";
    private static final String SUCCESS_KEY = "success";
    private final ApplicationStore applicationStore;
    private final UssdUserSessionRegistry ussdUserSessionRegistry;


    @Override
    public Map<String, Object> getUssdMessageDocuments(){
        Map<String, Object> result = getSuccessModelMap();
        MessageDocuments messageDocuments = (MessageDocuments) applicationStore.getItem(ApplicationItem.MESSAGE_DOCUMENTS.name());
        List<UssdMessageDocumentModel> messageDocumentList = messageDocuments.getMessageDocuments()
                .stream()
                .map(messageDocument -> {
                    UssdMessageDocumentModel model = new UssdMessageDocumentModel();
                    BeanUtils.copyProperties(messageDocument, model);
                    model.setFile(messageDocument.getFile().getAbsolutePath());
                    return model;
                }).collect(Collectors.toList());
        result.put(DATA_KEY, messageDocumentList);
        return result;
    }

    @Override
    public Map<String, Object> getAllConfiguredUssdExecutionContextMappings(){
        Map<String, Object> result = getSuccessModelMap();
        List<UssdExecutable> executables = SimpleUssdMappingRegistry.USSD_EXECUTION_CONTEXTS;
        List<UssdExecutionContextModel> models = new ArrayList<>();
        executables.forEach(ussdExecutable -> {
            if(ussdExecutable.supportExecutableType(UssdExecutableType.SOLE_EXECUTABLE)){
                SoleUssdExecutionContextWrapper executionContextWrapper = (SoleUssdExecutionContextWrapper)ussdExecutable;
                UssdExecutionContext executionContext = executionContextWrapper.getUssdExecutionContext();
                models.add(mapUssdExecutionContextToUssdExecutionContextModel(executionContext));
            }
            else if(ussdExecutable.supportExecutableType(UssdExecutableType.GROUP_EXECUTABLE)){
                GroupUssdExecutableContextWrapper contextWrapper = (GroupUssdExecutableContextWrapper) ussdExecutable;
                List<UssdExecutionContext> contexts = contextWrapper.getUssdExecutionContexts();
                List<UssdExecutionContextModel> contextModels = contexts.stream().map(this::mapUssdExecutionContextToUssdExecutionContextModel).collect(Collectors.toList());
                models.addAll(contextModels);
            }
        });
        result.put(DATA_KEY, models);
        return result;
    }

    @Override
    public Map<String, Object> getAllUssdUserExecutionContextBySessionId(String sessionId){
        UssdSession session = ussdUserSessionRegistry.getSession(sessionId);
        if(GeneralUtils.isNullOrEmpty(session)){
            Map<String, Object> error = getErrorMap();
            error.put(MESSAGE_KEY, String.format("No session found for sessionId %s", sessionId));
            error.put(DATA_KEY, new ArrayList<>());
            return error;
        }
        Map<String, Object> result = getSuccessModelMap();
        result.put(DATA_KEY, mapUssdSessionToUssdSessionModel(session));
        return result;
    }

    @Override
    public Map<String, Object> getAllCurrentRunningSessions(){
        List<UssdSessionModel> sessions = ussdUserSessionRegistry.getAllSessions().stream()
                .map(this::mapUssdSessionToUssdSessionModel)
                .collect(Collectors.toList());
        Map<String, Object> result = getSuccessModelMap();
        result.put(DATA_KEY, sessions);
        return result;
    }


    private UssdSessionModel mapUssdSessionToUssdSessionModel(UssdSession session){
        List<UssdUserExecutionContextModel> ussdUserExecutionContextModels = session.getExecutionContextChain()
                .stream()
                .map(this::mapUssdUserExecutionContextToUssdUserExecutionContextModel).collect(Collectors.toList());

        return UssdSessionModel.builder()
                .sessionData(session.getSessionData().getSessionRepo())
                .ussdModel(session.getUssdModel().getModelMap())
                .sessionId(session.getSessionId())
                .executionContextChain(new BackwardNavigableList<>(ussdUserExecutionContextModels))
                .createdAt(session.getCreatedAt().toString())
                .isFresh(session.isFresh())
                .build();
    }

    private UssdExecutionContextModel mapUssdExecutionContextToUssdExecutionContextModel(UssdExecutionContext executionContext){
        UssdExecutionContextModel executionContextModel = new UssdExecutionContextModel();
        BeanUtils.copyProperties(executionContext, executionContextModel);
        executionContextModel.setInvocableMethod(executionContext.getInvocableMethod().getName());
        return executionContextModel;
    }

    private UssdUserExecutionContextModel mapUssdUserExecutionContextToUssdUserExecutionContextModel(UssdUserExecutionContext ussdUserExecutionContext){
        UssdExecutionContext context = ussdUserExecutionContext.getExecutionContext();
        UssdExecutionContextModel contextModel = mapUssdExecutionContextToUssdExecutionContextModel(context);
        UssdUserExecutionContextModel executionContextModel = new UssdUserExecutionContextModel();
        executionContextModel.setExecutionContext(contextModel);
        BeanUtils.copyProperties(ussdUserExecutionContext, executionContextModel);
        return executionContextModel;
    }

    private static Map<String, Object> getSuccessModelMap(){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(STATUS_KEY, "success");
        map.put(SUCCESS_KEY, true);
        map.put(MESSAGE_KEY, "successful operation");
        return map;
    }

    private static Map<String, Object> getErrorMap(){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(STATUS_KEY, "bad-request");
        map.put(SUCCESS_KEY, "false");
        return map;
    }
}
