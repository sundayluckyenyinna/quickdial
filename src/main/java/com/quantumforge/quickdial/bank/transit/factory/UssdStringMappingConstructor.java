package com.quantumforge.quickdial.bank.transit.factory;

import com.quantumforge.quickdial.annotation.UssdMenuMapping;
import com.quantumforge.quickdial.annotation.UssdSubMenuMapping;

public interface UssdStringMappingConstructor {
    String constructStringMapping(UssdMenuMapping menuHandler, UssdSubMenuMapping subMenuHandler);
}
