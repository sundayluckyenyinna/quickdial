package com.quantumforge.quickdial.messaging;

import com.quantumforge.quickdial.messaging.bean.QuickDialMessageSourceConfig;
import com.quantumforge.quickdial.messaging.builder.impl.XmlUssdMessageSourceDocumentBuilder;
import com.quantumforge.quickdial.messaging.config.QuickDialMessageTemplateEngineConfigProperties;
import com.quantumforge.quickdial.messaging.starter.QuickDialMessageSourceStarter;
import com.quantumforge.quickdial.messaging.template.engine.MessageSourceContextManager;
import com.quantumforge.quickdial.messaging.template.resolvers.FreemarkerTemplateResolver;
import com.quantumforge.quickdial.messaging.template.resolvers.SimpleTemplateResolverRouter;
import com.quantumforge.quickdial.messaging.template.resolvers.ThymeleafTemplateResolver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfiguration
@Import(value = {
        XmlUssdMessageSourceDocumentBuilder.class,
        QuickDialMessageSourceConfig.class,
        QuickDialMessageSourceStarter.class,
        MessageSourceContextManager.class,
        FreemarkerTemplateResolver.class,
        ThymeleafTemplateResolver.class,
        SimpleTemplateResolverRouter.class
})
public class MessagingModule {
}
