package com.quantumforge.quickdial.test;

import com.quantumforge.quickdial.annotation.*;
import com.quantumforge.quickdial.bank.global.ApplicationItem;
import com.quantumforge.quickdial.bank.global.UssdBasicItemStore;
import com.quantumforge.quickdial.event.*;
import com.quantumforge.quickdial.messaging.template.engine.UssdMessageDocumentResolver;
import com.quantumforge.quickdial.payload.UssdExecution;
import com.quantumforge.quickdial.session.SessionData;
import com.quantumforge.quickdial.session.UssdModel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

import java.util.Arrays;
import java.util.List;


@UssdMenuHandler
@RequiredArgsConstructor
public class TestMapping {

    private final UssdBasicItemStore ussdItemStore;

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
