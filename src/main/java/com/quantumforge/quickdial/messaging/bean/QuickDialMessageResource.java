package com.quantumforge.quickdial.messaging.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuickDialMessageResource {
    private File primaryResourceFolder;
    private String name;
}
