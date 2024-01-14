package com.quantumforge.quickdial.messaging.template.engine;

import com.quantumforge.quickdial.session.UssdModel;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.NonNull;

public interface UssdMessageDocumentResolver {
    UssdMessageDocumentResolver withModel(UssdModel ussdModel);

    UssdMessageDocumentResolver withSessionId(String sessionId);

    UssdMessageDocumentResolver withSession(@NonNull UssdSession session);

    String getResolvedMessageById(String messageId);

    String getResolvedMessageById(String messageId, String separator);
}
