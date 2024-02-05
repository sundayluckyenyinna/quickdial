package com.quantumforge.quickdial.util;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfiguration
@Import(value = { QuickDialUtil.class })
public class UtilModule {
}
