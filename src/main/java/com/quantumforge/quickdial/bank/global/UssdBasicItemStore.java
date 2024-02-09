package com.quantumforge.quickdial.bank.global;

import com.quantumforge.quickdial.bootstrap.CommonUssdConfigProperties;
import com.quantumforge.quickdial.event.UssdMappingExecutionContextInitializedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UssdBasicItemStore {

    private final CommonUssdConfigProperties configProperties;
    private static final Map<ApplicationItem, Object> USSD_ITEM_STORE =  new LinkedHashMap<>();

    public Object getItem(ApplicationItem item){
        return USSD_ITEM_STORE.get(item);
    }
    public <T> T getItem(ApplicationItem item, Class<T> tClass){
        Object value = getItem(item);
        return Objects.nonNull(value) ? tClass.cast(value) : null;
    }

    private void setItem(ApplicationItem item, Object itemValue){
        USSD_ITEM_STORE.put(item, itemValue);
    }

    @EventListener(value = UssdMappingExecutionContextInitializedEvent.class)
    public void configureUssdStore(){
        log.info("Starting Initialization of Ussd common configuration properties to UssdBasicItemStore");
        setItem(ApplicationItem.USSD_BASE_CODE, configProperties.getBaseUssdCode());
        setItem(ApplicationItem.USSD_GO_BACK_OPTION, configProperties.getGoBackOption());
        setItem(ApplicationItem.USSD_GO_FORWARD_OPTION, configProperties.getGoForwardOption());
        setItem(ApplicationItem.ACCEPTABLE_INPUT_TRIAL_TIMES, configProperties.getAcceptableInputTrialTimes());
        log.info("Finished Initialization of Ussd common configuration properties to UssdBasicItemStore");
    }
}
