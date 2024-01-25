package com.quantumforge.quickdial.messaging.template.resolvers;

import com.quantumforge.quickdial.util.GeneralUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleTemplateResolverRouter implements TemplateResolverRouter{

    private final List<TemplateResolver> templateResolvers;

    @Override
    public String resolveTemplateByEngine(String rawTemplate, Map<String, Object> model, String engineStr) throws Exception {
        ModelTemplateEngine engine = getTemplateEngine(engineStr);
        return templateResolvers.stream()
                .filter(templateResolver -> templateResolver.supportsEngine(engine))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No template resolver engine found for given engine string: %s", engineStr)))
                .resolve(rawTemplate, model);
    }

    private ModelTemplateEngine getTemplateEngine(String engineStr){
        if(!GeneralUtils.isNullOrEmpty(engineStr)){
            engineStr = engineStr.trim().toUpperCase();
            try{
                return ModelTemplateEngine.valueOf(engineStr);
            }catch (Exception exception){
                return ModelTemplateEngine.THYMELEAF;
            }
        }
        return ModelTemplateEngine.THYMELEAF;
    }
}
