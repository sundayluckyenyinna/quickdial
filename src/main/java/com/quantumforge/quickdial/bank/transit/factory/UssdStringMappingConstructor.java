package com.quantumforge.quickdial.bank.transit.factory;

import com.quantumforge.quickdial.annotation.UssdMenuHandler;
import com.quantumforge.quickdial.annotation.UssdSubMenuHandler;

public interface UssdStringMappingConstructor {
    String constructStringMapping(UssdMenuHandler menuHandler, UssdSubMenuHandler subMenuHandler);
}
