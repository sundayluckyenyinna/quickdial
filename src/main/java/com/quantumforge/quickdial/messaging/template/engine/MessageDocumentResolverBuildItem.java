package com.quantumforge.quickdial.messaging.template.engine;

import com.quantumforge.quickdial.bank.global.UssdBasicItemStore;
import com.quantumforge.quickdial.bootstrap.CommonUssdConfigProperties;
import com.quantumforge.quickdial.messaging.template.resolvers.TemplateResolverRouter;
import com.quantumforge.quickdial.messaging.template.strut.MessageDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDocumentResolverBuildItem {
    private String preferredEngine;
    private MessageDocument messageDocument;
    private TemplateResolverRouter templateResolverRouter;
    private UssdBasicItemStore ussdBasicItemStore;
    private CommonUssdConfigProperties ussdConfigProperties;
}
