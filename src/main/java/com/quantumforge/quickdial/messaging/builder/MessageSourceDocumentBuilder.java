package com.quantumforge.quickdial.messaging.builder;

import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.messaging.template.strut.FileResource;
import com.quantumforge.quickdial.messaging.template.strut.MessageDocument;

import java.util.Arrays;
import java.util.List;

public interface MessageSourceDocumentBuilder {

    String THYMELEAF_AUTOMATIC_ERROR_LINE_TEMPLATE =
                    "<line/>" +
                    "<line th:if=\"${isRedirectForParamValidationError}\">%s</line>" + // param validation check
                    "<line th:if=\"${isRedirectForOptionValidationError}\">%s</line>";   // option check

    String FREEMARKER_AUTOMATIC_ERROR_LINE_TEMPLATE =
                    "<line/>" +
                    "<line><#if isRedirectForParamValidationError>%s</#if></line>" + // param validation check
                    "<line><#if isRedirectForOptionValidationError>%s</#if></line>"; // option check


    String INPUT_VALIDATION_ERROR_PLACE_HOLDER = "x_input_validation_error_place_holder";
    String THYMELEAF_INPUT_VALIDATION_ERROR_TEMPLATE = String.format("[[${%s}]]", INPUT_VALIDATION_ERROR_PLACE_HOLDER);
    String FREEMARKER_INPUT_VALIDATION_ERROR_TEMPLATE = String.format("${%s}", INPUT_VALIDATION_ERROR_PLACE_HOLDER);

    boolean supportsDocumentType(DocumentType documentType);
    MessageDocument buildDocument(FileResource fileResources);

    default DocumentType getDocumentTypeByExtension(String ext){
        List<DocumentType> documentTypes = Arrays.asList(DocumentType.values());
        return documentTypes
                .stream()
                .filter(documentType -> ext.endsWith(documentType.getExtension()))
                .findFirst()
                .orElse(DocumentType.UNSUPPORTED);
    }
}
