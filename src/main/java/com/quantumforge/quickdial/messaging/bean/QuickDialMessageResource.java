package com.quantumforge.quickdial.messaging.bean;

import com.quantumforge.quickdial.messaging.template.strut.FileResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuickDialMessageResource {
    private List<FileResource> fileResources = new ArrayList<>();
    private String name;
}
