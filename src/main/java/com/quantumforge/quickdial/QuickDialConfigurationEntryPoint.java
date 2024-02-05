package com.quantumforge.quickdial;

import com.quantumforge.quickdial.advice.AdviceModule;
import com.quantumforge.quickdial.bank.BankModule;
import com.quantumforge.quickdial.context.ContextModule;
import com.quantumforge.quickdial.event.EventModule;
import com.quantumforge.quickdial.execution.ExecutionModule;
import com.quantumforge.quickdial.interceptor.InterceptorModule;
import com.quantumforge.quickdial.messaging.MessagingModule;
import com.quantumforge.quickdial.stats.StatisticsModule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfiguration
@Import(value = {
        AdviceModule.class,
        BankModule.class,
        ContextModule.class,
        EventModule.class,
        ExecutionModule.class,
        InterceptorModule.class,
        MessagingModule.class,
        StatisticsModule.class
})
public class QuickDialConfigurationEntryPoint {
}
