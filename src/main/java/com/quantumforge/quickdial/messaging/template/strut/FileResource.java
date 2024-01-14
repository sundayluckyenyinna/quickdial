package com.quantumforge.quickdial.messaging.template.strut;

import com.quantumforge.quickdial.common.StringValues;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResource {
    private File file;
    private String qualifiedName;
    public String getFileExtension(){
        int lastIndexOfDot = file.getName().lastIndexOf(StringValues.DOT);
        return file.getName().substring(lastIndexOfDot + 1);
    }
}
