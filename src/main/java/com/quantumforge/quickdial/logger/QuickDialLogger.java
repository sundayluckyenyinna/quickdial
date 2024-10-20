package com.quantumforge.quickdial.logger;

import com.quantumforge.quickdial.util.QColor;
import com.quantumforge.quickdial.util.GeneralUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class QuickDialLogger {

    private static final String QCOLOR_SUFFIX = "\u001B[0m";

    public static void logInfo(String message, QColor qColor, String ...values){
        if(GeneralUtils.isNullOrEmpty(qColor))
            log.info(message, (Object) values);
        else {
            List<String> coloredValues = getColoredMessageValues(qColor, values);
            log.info(message, coloredValues);
        }
    }

    public static void logInfo(String message){
        logInfo(message, null);
    }

    public static void logError(String message, QColor qColor, String ...values){
        if(GeneralUtils.isNullOrEmpty(qColor))
            log.error(message, (Object) values);
        else {
            List<String> coloredValues = getColoredMessageValues(qColor, values);
            log.info(message, coloredValues);
        }
    }

    public static void logError(String message){
        logError(message, null);
    }

    public static void logDebug(String message, QColor qColor, String ...values){
        if(GeneralUtils.isNullOrEmpty(qColor))
            log.debug(message, (Object) values);
        else {
            List<String> coloredValues = getColoredMessageValues(qColor, values);
            log.info(message, coloredValues);
        }
    }

    public static void logDebug(String message){
        logDebug(message, null);
    }

    private static List<String> getColoredMessageValues(QColor qColor, String ...values){
        return Arrays.stream(values).map(val -> qColor.getAnsiCode().concat(val).concat(QCOLOR_SUFFIX)).collect(Collectors.toList());
    }
}
