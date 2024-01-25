package com.quantumforge.quickdial.messaging.config;

public interface QuickDialMessageSourceConfigDefaultProperties {
    String DEFAULT_TEMPLATE_PATH = "quickdial";
    boolean DEFAULT_VERBOSE_TEMPLATE_LOGGING = true;
    String DEFAULT_NESTED_FILE_SEPARATOR = "UNDER_SCORE";


    // XML CONFIGS
    String XML_MESSAGE_TAG = "message";
    String XML_LINE_TAG = "line";
    String XML_MESSAGE_ID = "id";
    String XML_MESSAGE_OPTION = "option";
    String XML_LINE_TAG_RAW = "<line></line>";
    String XML_LINE_TAGE_RAW_SELF_CLOSING = "<line/>";
}
