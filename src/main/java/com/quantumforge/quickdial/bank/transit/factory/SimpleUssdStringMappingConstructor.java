package com.quantumforge.quickdial.bank.transit.factory;

import com.quantumforge.quickdial.annotation.UssdMenuHandler;
import com.quantumforge.quickdial.annotation.UssdSubMenuHandler;
import com.quantumforge.quickdial.bootstrap.CommonUssdConfigProperties;
import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.execution.provider.UssdInvocationType;
import com.quantumforge.quickdial.util.GeneralUtils;
import com.quantumforge.quickdial.util.QuickDialUtil;
import com.quantumforge.quickdial.util.UssdUtilProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = CommonUssdConfigProperties.class)
public class SimpleUssdStringMappingConstructor implements UssdStringMappingConstructor{

    private final QuickDialUtil quickDialUtil;
    private final UssdUtilProperties ussdUtilProperties;
    private final CommonUssdConfigProperties ussdConfigProperties;
    @Override
    public String constructStringMapping(UssdMenuHandler menuHandler, UssdSubMenuHandler subMenuHandler){
        List<String> tokens = new ArrayList<>(quickDialUtil.getTokensBetweenDelimiters(ussdConfigProperties.getBaseUssdCode()));
        List<String> finalTokens = tokens;
        GeneralUtils.doIf(menuHandler.type() == UssdInvocationType.SHORT_CODE, ()-> finalTokens.add(ussdUtilProperties.getShortCodePrefix()));
        tokens.add(GeneralUtils.returnOrDefaultWithEmptyString(menuHandler.prefix()).trim());
        tokens.add(GeneralUtils.returnOrDefaultWithEmptyString(menuHandler.menu()).trim());
        tokens.add(GeneralUtils.returnOrDefaultWithEmptyString(subMenuHandler.submenu()).trim());
        tokens = tokens.stream().filter(string -> Objects.nonNull(string) && !string.trim().isEmpty()).collect(Collectors.toList());
        String stringMapping = String.join(ussdUtilProperties.getStartDelimiter(), tokens);
        String chainedMapping = GeneralUtils.replaceConsecutiveTokens(stringMapping, ussdUtilProperties.getStartDelimiter());
        String mapping = ussdUtilProperties.getStartDelimiter().concat(chainedMapping).concat(ussdUtilProperties.getEndDelimiter());
        String cleanedMapping = mapping.replace(StringValues.BACKWARD_SLASH, StringValues.EMPTY_STRING);
        cleanedMapping = GeneralUtils.replaceConsecutiveTokens(cleanedMapping, ussdUtilProperties.getStartDelimiter());
        cleanedMapping = GeneralUtils.replaceConsecutiveTokens(cleanedMapping, ussdUtilProperties.getEndDelimiter());
        cleanedMapping = GeneralUtils.replaceConsecutiveTokens(cleanedMapping, ussdUtilProperties.getShortCodePrefix());
        return cleanedMapping.replaceAll(StringValues.SINGLE_SPACE, StringValues.EMPTY_STRING);
    }
}
