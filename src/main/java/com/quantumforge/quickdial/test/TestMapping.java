package com.quantumforge.quickdial.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantumforge.quickdial.annotation.*;
import com.quantumforge.quickdial.context.UserUssdContext;
import com.quantumforge.quickdial.execution.provider.UssdInvocationType;
import com.quantumforge.quickdial.messaging.template.engine.UssdMessageDocumentResolver;
import com.quantumforge.quickdial.session.SessionData;
import com.quantumforge.quickdial.session.UssdModel;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


@UssdMenuHandler
public class TestMapping {

    @InjectDocument("quickdial")
    private UssdMessageDocumentResolver documentResolver;

    @UssdSubMenuHandler
    public String showStartPageOfCharges(UssdModel model){
        return documentResolver.withModel(model).getResolvedMessageById("charge");
    }

    @UssdSubMenuHandler(submenu = "*{chargeOption}")
    public String showMenus(@UssdParam("chargeOption") String chargeOption, UssdModel model, SessionData sessionData){
        if(chargeOption.equalsIgnoreCase("1")){
            sessionData.keepAttribute("here", chargeOption);
            return documentResolver.withModel(model).getResolvedMessageById("menus");
        }
        return "End of session";
    }

    @UssdGroupMapping(id = "account-type-group", order = 1)
    @UssdSubMenuHandler(submenu = "*{chargeOption}*1")
    public String showPageBasedOnMenu(UssdModel model, @SessionValue("here") String here){
        return documentResolver.withModel(model).getResolvedMessageById("accounts");
    }

    @UssdGroupMapping(id = "account-type-group", order = 2)
    @UssdSubMenuHandler(submenu = "*{chargeOption}*1")
    public String showPageBasedOnMenu2(UssdModel model){
        return documentResolver.withModel(model).getResolvedMessageById("accounts-next");
    }

    @UssdSubMenuHandler(submenu = "*{chargeOption}*1*{option}")
    public String showPageBasedOnMenu3(UssdModel model){
        return documentResolver.withModel(model).getResolvedMessageById("accounts-msg");
    }
}
