package com.quantumforge.quickdial.util;

import com.quantumforge.quickdial.common.StringValues;

import java.util.Objects;
import java.util.regex.Pattern;

public class GeneralUtils {

    public final static int MESSAGE_PADDING = 3;

    public static <T> T returnValueOrDefaultWith(T value, T defaultValue){
        return Objects.nonNull(value) ? value : defaultValue;
    }

    public static String returnOrDefaultWithEmptyString(String value){
        return returnValueOrDefaultWith(value, StringValues.EMPTY_STRING);
    }

    public static String writeWithLeftPadding(int leftPadding, String string){
        return writeEmptySpaceBy(leftPadding).concat(string);
    }

    public static String writeWithRightPadding(int rightPadding, String string){
        return string.concat(writeEmptySpaceBy(rightPadding));
    }

    public static String writeWithPadding(int leftPadding, int rightPadding, String string){
        return writeEmptySpaceBy(leftPadding).concat(string).concat(writeEmptySpaceBy(rightPadding));
    }

    public static String writeEmptySpaceBy(int padding){
        return StringValues.SINGLE_SPACE.repeat(Math.max(0, padding));
    }

    public static String replaceConsecutiveTokens(String input, String token) {
        String escapedToken = Pattern.quote(token);
        return input.replaceAll(escapedToken + "+", token);
    }

    public static void doIf(boolean condition, NoInputOperation noInputOperation){
        if(condition){
            noInputOperation.execute();
        }
    }

    public static boolean isNullOrEmpty(Object value){
        return Objects.isNull(value) || String.valueOf(value).trim().isEmpty();
    }

    public static String getPluralisedRetry(int times){
        return times > 1 ? "retries" : "retry";
    }
}
