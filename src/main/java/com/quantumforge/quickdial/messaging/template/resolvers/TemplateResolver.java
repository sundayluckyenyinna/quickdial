package com.quantumforge.quickdial.messaging.template.resolvers;

import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;

public interface TemplateResolver {

    String resolve(String rawTemplate, Map<String, Object> model) throws Exception;
    boolean supportsEngine(ModelTemplateEngine engine);
}
