package com.quantumforge.quickdial.common;

public interface StringValues {

    String BEAN_CREATION_SUCCESS = "SUCCESS";
    String EMPTY_STRING = "";
    String UNDERSCORE = "_";
    String DOT = ".";
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
    String REDIRECT_REFERENCE_TOKEN = "::";
    String FORWARD_SLASH = "/";

    String EQUALS = "=";
    String ATT = "@";
    String EXCLAMATION = "!";
    String HASH = "#";
    String DOLLAR = "$";
    String ESC_DOLLAR = "\\$";
    String PERCENT = "%";
    String POWER_STAND = "^";
    String AMPER_STAND = "&";
    String ESC_STAR = "\\*";
    String OPEN_PARENTHESIS = "(";
    String CLOSED_PARENTHESIS = ")";
    String HYPHEN = "-";
    String UNDER_SCORE = "_";
    String PLUS = "+";
    String GT = ">";
    String LT = "<";
    String COLON = ":";
    String SEMI_COLON = ";";
    String FORWARD_STROKE = "/";
    String BACKWARD_STROKE = "\\";
    String QUESTION_MARK = "?";
    String PERIOD = ".";
    String OPEN_BLOCK = "[";
    String CLOSED_BLOCK = "]";
    String OPEN_BRACKET = "{";
    String CLOSED_BRACKET = "}";
    String COMMA = ",";
    String STROKE = "|";
    String STAR = "*";
    String START_LIST_CHAR = "[";
    String END_LIST_CHAR = "]";

    // configuration values
    String AFRICA_LAGOS_ZONE = "Africa/Lagos";
    String BEARER_PREFIX = "Bearer ";
    String APP_USER_KEY = "appUser";
    String ENC_KEY_PLACEHOLDER = "encKey";
    String APP_USER_REQUIRE_ENCY_KEY = "ENC_REQUIRED";
    String REQUEST_BODY_KEY = "REQUEST_BODY";
    String AUTH_KEY_BEARER_PREFIX = "Bearer ";
    String AUTH_HEADER_KEY = "Authorization";
    String ID_TOKEN_KEY = "idToken";
    String SINGLE_QUOTE = "\"";
    String TAB = "\t";

    // Patterns
    String SWAGGER_FOX_PATTERN = "/swagger-resources/**";
    String SWAGGER_PATTERN = "/**/swagger-ui/**";
    String V3_SWAGGER_PATTERN = "/**/v3/api-docs/swagger-config/**";
    String V3_API_DOCS_PATTERN = "/**/v3/api-docs/**";
    String LOCAL_IPV4_IPV6_ADDRESS = "[127.0.0.1,0:0:0:0:0:0:0:1]";


    // Default Values
    String PAGE_NUMBER_KEY = "pageNumber";
    String PAGE_SIZE_KEY = "pageSize";
    String DEFAULT_PAGE_NUMBER = "1";
    String DEFAULT_PAGE_SIZE = "10";
    String MAX_DEFAULT_PAGE_SIZE = "100";
}
