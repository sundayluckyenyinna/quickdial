package com.quantumforge.quickdial.messaging.template.instrumentation;

import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.util.GeneralUtils;
import com.quantumforge.quickdial.util.QuickDialUtil;

import java.util.Objects;
import java.util.regex.Pattern;

public class OptionLineWriter extends AbstractLineWriter{

    private OptionLineWriter(){}

    public static OptionLineWriter start(){
        return new OptionLineWriter();
    }

    public OptionLineWriter addLine(String option, String message, String separator){
        GeneralUtils.doIf(isValidOption(option), () -> {
            String completeLine = String.join(separator, option, message);
            lines.add(completeLine.replaceAll("\"", StringValues.EMPTY_STRING));
        });
        GeneralUtils.doIf(!isValidOption(option), () -> lines.add(message));
        return this;
    }

    public OptionLineWriter addLine(String option, String message){
        return isValidOption(option) ? addLine(option, message, StringValues.DOT_SPACE) : addLine(StringValues.EMPTY_STRING, message, StringValues.EMPTY_STRING);
    }

    public OptionLineWriter addLine(String option, String message, boolean useDefaultSeparator){
        return isValidOption(option) ? addLine(option, message, useDefaultSeparator ? StringValues.DOT_SPACE : StringValues.EMPTY_STRING) : addLine(StringValues.EMPTY_STRING, message, StringValues.EMPTY_STRING);
    }

    public static boolean isValidOption(String option){
        return Objects.nonNull(option) && !option.trim().isEmpty();
    }
}
