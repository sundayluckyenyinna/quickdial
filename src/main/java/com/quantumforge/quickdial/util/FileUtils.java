package com.quantumforge.quickdial.util;

import com.quantumforge.quickdial.common.StringValues;
import com.quantumforge.quickdial.messaging.template.strut.FileResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class FileUtils {

    public static List<File> getAllFilesInFolder(String folderResourceAbsPath, boolean recursive){
        File folder = new File(folderResourceAbsPath);
        if(folder.exists()) {
            if (!recursive) {
                return List.of(Objects.requireNonNull(folder.listFiles()));
            }
            return getFilesInFolder(folder);
        }
        return Collections.emptyList();
    }

    public static List<File> getFilesInFolder(File folder){
        List<File> result = new ArrayList<>();
        File[] files = folder.listFiles();
        if(files == null){
            return new ArrayList<>();
        }
        Arrays.stream(files).forEach(file -> {
            if(file.isFile()){
                result.add(file);
            }else{
                result.addAll(getFilesInFolder(file));
            }
        });
        return result;
    }

    public static List<FileResource> getFileResourcesInBaseFolder(File folder, String nestedFileSeparator){
        return getFilesInFolder(folder)
                .stream()
                .map(file -> {
                    try {
                        return FileResource.builder()
                                .fileName(file.getName())
                                .resourceFilePath(file.getAbsolutePath())
                                .inputStream(new FileInputStream(file))
                                .qualifiedName(getQualifiedNameOfFileRelativeToFolder(file, folder.getAbsolutePath(), nestedFileSeparator))
                                .build();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public static String getQualifiedNameOfFileRelativeToFolder(File file, String folderAbsPath, String nestedFileSeparator){
        try {
            String absolutePath = file.getAbsolutePath();
            String relPath = absolutePath.replace(folderAbsPath, StringValues.EMPTY_STRING);
            List<String> tokens = Arrays.stream(relPath.split("\\".concat(File.separator)))
                    .filter(token -> Objects.nonNull(token) && !StringUtils.isEmpty(token.trim()))
                    .collect(Collectors.toList());
            String joined = String.join(nestedFileSeparator, tokens);
            return joined.substring(0, joined.lastIndexOf(StringValues.DOT));
        }catch (Exception exception){
            log.error(exception.getMessage());
            return StringValues.EMPTY_STRING;
        }
    }

    public static void copyInputStreamToFile(InputStream inputStream, File file){
        try (OutputStream output = new FileOutputStream(file)) {
            inputStream.transferTo(output);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
