package com.quantumforge.quickdial.messaging.template.engine;

import com.quantumforge.quickdial.bank.transit.impl.SimpleUssdUserSessionRegistry;
import com.quantumforge.quickdial.session.UssdModel;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@Getter
public class DefaultUssdMessageDocumentResolver implements UssdMessageDocumentResolver{

    protected MessageDocumentResolverBuildItem buildItem;

    public DefaultUssdMessageDocumentResolver(MessageDocumentResolverBuildItem buildItem){
        this.buildItem = buildItem;
    }

    @Override
    public ModelUssdMessageResolver withModel(UssdModel ussdModel){
        return new ModelUssdMessageResolver(this.buildItem, ussdModel);
    }

    @Override
    public ModelUssdMessageResolver withSessionId(String sessionId){
        UssdSession session = SimpleUssdUserSessionRegistry.getSessionStatically(sessionId);
        if(Objects.nonNull(session)){
            return new ModelUssdMessageResolver(this.buildItem, session.getUssdModel());
        }
        throw new IllegalArgumentException(String.format("No session found with sessionId: %s", sessionId));
    }

    @Override
    public ModelUssdMessageResolver withSession(@NonNull UssdSession session){
        return this.withModel(session.getUssdModel());
    }
}
