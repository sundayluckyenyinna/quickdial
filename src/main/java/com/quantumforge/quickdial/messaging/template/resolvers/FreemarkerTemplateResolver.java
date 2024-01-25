package com.quantumforge.quickdial.messaging.template.resolvers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.StringWriter;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FreemarkerTemplateResolver implements TemplateResolver{

    @Override
    public String resolve(String rawTemplate, Map<String, Object> model) throws Exception {
        freemarker.template.Configuration configuration = new freemarker.template.Configuration(freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        freemarker.template.Template template = new freemarker.template.Template(null, rawTemplate, configuration);
        StringWriter writer = new StringWriter();
        template.process(model, writer);
        return writer.toString();
    }

    @Override
    public boolean supportsEngine(ModelTemplateEngine engine) {
        return engine == ModelTemplateEngine.FREEMARKER;
    }
}
