package com.quantumforge.quickdial.stats.model;

import com.quantumforge.quickdial.messaging.template.strut.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UssdMessageDocumentModel {
    private String file;
    private String fileName;
    private String qualifiedName;
    private List<Message> messages;
}
