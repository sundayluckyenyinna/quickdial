package com.quantumforge.quickdial.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantumforge.quickdial.annotation.*;
import com.quantumforge.quickdial.bank.global.ApplicationItem;
import com.quantumforge.quickdial.bank.global.UssdItemStore;
import com.quantumforge.quickdial.context.UserUssdContext;
import com.quantumforge.quickdial.event.*;
import com.quantumforge.quickdial.execution.provider.UssdInvocationType;
import com.quantumforge.quickdial.messaging.bean.QuickDialMessageResource;
import com.quantumforge.quickdial.messaging.template.engine.UssdMessageDocumentResolver;
import com.quantumforge.quickdial.messaging.template.strut.MessageDocuments;
import com.quantumforge.quickdial.payload.UssdExecution;
import com.quantumforge.quickdial.session.SessionData;
import com.quantumforge.quickdial.session.UssdModel;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


@UssdMenuHandler
@RequiredArgsConstructor
public class TestMapping {

    private final UssdItemStore ussdItemStore;

    @InjectDocument("home_quickdial")
    private UssdMessageDocumentResolver documentResolver;

    @UssdSubMenuHandler
    public String showStartPageOfCharges(UssdModel model){
        System.out.println("This is the starting" + ussdItemStore.getItem(ApplicationItem.USSD_GO_FORWARD_OPTION));
        model.addObject("firstObject", "First");
        model.addObject("secondObject", "Secod");
        List<String> list = Arrays.asList("Mango", "Apple", "Orange");
        model.addObject("names", list);
        return documentResolver.withModel(model).getResolvedMessageById("charge");
    }

    @UssdSubMenuHandler(submenu = "*{chargeOption}")
    public UssdExecution<String> showMenus(@UssdParam("chargeOption") String chargeOption, UssdModel model, SessionData sessionData){
        if(chargeOption.equalsIgnoreCase("1")){
            sessionData.keepAttribute("here", chargeOption);
            return UssdExecution.continues(documentResolver.withModel(model).getResolvedMessageById("menus"));
        }
        return UssdExecution.redirect("this::showStartPageOfCharges");
    }

    @UssdGroupMapping(id = "account-type-group", order = 1)
    @UssdSubMenuHandler(submenu = "* { chargeOption } * { accOp }")
    public String showPageBasedOnMenu(UssdModel model, @SessionValue("here") String here, @UssdParam String accOp){
        return documentResolver.withModel(model).getResolvedMessageById("accounts");
    }

    @UssdGroupMapping(id = "account-type-group", order = 2)
    @UssdSubMenuHandler(submenu = "* { chargeOption } * { accOp }")
    public String showPageBasedOnMenu2(UssdModel model){
        return documentResolver.withModel(model).getResolvedMessageById("accounts-next");
    }

    @UssdGroupMapping(id = "account-type-group1", order = 3)
    @UssdSubMenuHandler(submenu = "* { chargeOption } * { accOp } * { option } #")
    public UssdExecution<String> showPageBasedOnMenu3(UssdModel model, @UssdParam String accOp, @UssdParam String option){
        System.out.println("Account option: " + option);
        if(option.equals("1") || option.equals("2")) {
            return UssdExecution.continues(documentResolver.withModel(model).getResolvedMessageById("accounts-msg"));
        }
        return UssdExecution.continues("Success and go ahead");
    }

    @EventListener(value = UssdUserSessionPostDestroyedEvent.class)
    public void sessionEnds(UssdUserSessionPostDestroyedEvent event){
        System.out.println(event.getSource());
        System.out.println("Session destroyed successfully...");
    }
}
