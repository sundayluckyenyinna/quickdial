package com.quantumforge.quickdial.messaging.template.resolvers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ThymeleafTemplateResolver implements TemplateResolver{
    private static final TemplateEngine TEMPLATE_ENGINE = new TemplateEngine();

    @Override
    public String resolve(String rawTemplate, Map<String, Object> model) throws Exception{
        Context context = new Context();
        model.forEach(context::setVariable);
        return TEMPLATE_ENGINE.process(rawTemplate, context);
    }

    @Override
    public boolean supportsEngine(ModelTemplateEngine engine) {
        return engine == ModelTemplateEngine.THYMELEAF;
    }
}
