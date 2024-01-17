package com.quantumforge.quickdial.messaging.builder;

import com.quantumforge.quickdial.messaging.template.strut.FileResource;
import com.quantumforge.quickdial.messaging.template.strut.MessageDocument;

import java.util.Arrays;
import java.util.List;

public interface MessageSourceDocumentBuilder {

    String AUTOMATIC_ERROR_LINE_TEMPLATE = "<line th:if=\"${isRedirectForOptionValidationError}\"></line>";

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
