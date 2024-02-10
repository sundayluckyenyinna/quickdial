package com.quantumforge.quickdial.messaging.template.engine;

import com.quantumforge.quickdial.session.UssdModel;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.NonNull;

public interface UssdMessageDocumentResolver {
    ModelUssdMessageResolver withModel(UssdModel ussdModel);

    ModelUssdMessageResolver withSessionId(String sessionId);

    ModelUssdMessageResolver withSession(@NonNull UssdSession session);
}
