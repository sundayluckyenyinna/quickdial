package com.quantumforge.quickdial.common;

public interface StringValues {

    String BEAN_CREATION_SUCCESS = "SUCCESS";
    String EMPTY_STRING = "";
    String UNDERSCORE = "_";
    String DOT = ".";
    String COMMA = ",";
    String NEW_LINE = "\n";
    String SINGLE_SPACE = " ";
    String DOT_SPACE = ". ";
    String OPENING_BRACE = "{";
    String CLOSING_BRACE = "}";
    String PARAMETERIZED_PATTERN = "\\{.*?\\}";
    String SIMPLE_PARAMETERIZED_PATTERN = "\\{.*?}";
    String BACKWARD_SLASH = "\\";
    String BRACE_PATTERN = "[{}]";

    String DOUBLE__OPENING_BLOCK = "[[ ";
    String DOUBLE_CLOSING_BLOCK = " ]]";
    String THYMELEAF_VALUE_MATCHER_PATTERN = "\\$\\{([^}]+)}";
}
