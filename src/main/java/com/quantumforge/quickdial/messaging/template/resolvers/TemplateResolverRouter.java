package com.quantumforge.quickdial.messaging.template.resolvers;

import java.util.Map;

public interface TemplateResolverRouter {
    String resolveTemplateByEngine(String rawTemplate, Map<String, Object> model, String engineStr) throws Exception;
}
