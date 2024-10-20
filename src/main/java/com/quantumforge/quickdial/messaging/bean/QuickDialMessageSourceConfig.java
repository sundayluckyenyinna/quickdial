package com.quantumforge.quickdial.messaging.bean;

import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.messaging.config.QuickDialMessageSourceConfigurationProperties;
import com.quantumforge.quickdial.messaging.template.strut.FileResource;
import com.quantumforge.quickdial.util.FileUtils;
import com.quantumforge.quickdial.util.GeneralUtils;
import com.quantumforge.quickdial.util.ResourceProtocol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Configuration
@AutoConfiguration
@RequiredArgsConstructor
public class QuickDialMessageSourceConfig {

    private final QuickDialMessageSourceConfigurationProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public QuickDialMessageResource defaultQuickDialMessageResource() {
        try {
            if (!GeneralUtils.isNullOrEmpty(properties.getTemplatePath())) {
                return QuickDialMessageResource.builder()
                        .fileResources(getClassPathUssdMessageDocumentFolder())
                        .name(StringValues.EMPTY_STRING)
                        .build();
            }
            return new QuickDialMessageResource();
        }catch (Exception exception){
            log.error("Base message root folder source initialization error: {}", exception.getMessage());
            return null;
        }
    }

    private List<FileResource> getClassPathUssdMessageDocumentFolder() throws IOException {
        List<FileResource> fileResources = new ArrayList<>();
        ResourcePatternResolver resourcePatResolver = new PathMatchingResourcePatternResolver();
        String resources = "classpath*:".concat(properties.getTemplatePath());
        Resource[] messageResources = resourcePatResolver.getResources(resources);
        for(Resource resource : messageResources){
            try {
                URL resourceUrl = resource.getURL();
                log.info("Detected package protocol: {}", resourceUrl.getProtocol().toUpperCase());
                if(resourceUrl.getProtocol().equalsIgnoreCase(ResourceProtocol.FILE.name())){
                    File resourceFolder = resource.getFile();
                    List<FileResource> res = FileUtils.getFileResourcesInBaseFolder(resourceFolder, properties.getNestedFileSeparator());
                    fileResources.addAll(res);
                }
                else if(resourceUrl.getProtocol().equalsIgnoreCase(ResourceProtocol.JAR.name())){
                    JarURLConnection jarConnection = (JarURLConnection) resource.getURL().openConnection();
                    JarFile jarFile = jarConnection.getJarFile();
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()){
                        JarEntry jarEntry = entries.nextElement();
                        String entryName = jarEntry.getName();
                        String altTemplatePath = properties.getTemplatePath();
                        if((entryName.startsWith(properties.getTemplatePath()) || entryName.startsWith(altTemplatePath)
                            && !jarEntry.isDirectory())){
                            try {
                                String relativePath = entryName.replace(altTemplatePath, StringValues.EMPTY_STRING).trim();
                                List<String> tokens = Stream.of(relativePath.split(StringValues.FORWARD_SLASH))
                                        .filter(token -> !GeneralUtils.isNullOrEmpty(token.trim()))
                                        .collect(Collectors.toList());
                                String simpleName = entryName.substring(entryName.lastIndexOf(StringValues.FORWARD_SLASH) + 1);
                                String joined = String.join(properties.getNestedFileSeparator(), tokens);
                                String qualifiedBeanName = joined.substring(0, joined.lastIndexOf(StringValues.DOT));
                                FileResource fileResource = FileResource.builder()
                                        .fileName(simpleName)
                                        .resourceFilePath(entryName)
                                        .inputStream(jarFile.getInputStream(jarEntry))
                                        .qualifiedName(qualifiedBeanName)
                                        .build();
                                fileResources.add(fileResource);
                            }catch (Exception ignored){}
                        }
                    }
                }
                else {
                    throw new RuntimeException(String.format("Invalid path resource protocol: %s", resource.getURL().getProtocol()));
                }
            } catch (IOException ignored) {
            }
        }
        return fileResources;
    }
}
