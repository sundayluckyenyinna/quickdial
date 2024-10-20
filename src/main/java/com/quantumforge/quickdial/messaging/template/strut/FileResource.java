package com.quantumforge.quickdial.messaging.template.strut;

import com.quantumforge.quickdial.common.StringValues;
import lombok.*;

import java.io.File;
import java.io.InputStream;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FileResource {

    private String fileName;
    private String resourceFilePath;
    private InputStream inputStream;
    private String qualifiedName;

    public String getFileExtension(){
        int lastIndexOfDot = fileName.lastIndexOf(StringValues.DOT);
        return fileName.substring(lastIndexOfDot + 1);
    }
}
