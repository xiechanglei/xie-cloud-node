package com.xiechanglei.code.cloud.node.videos;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FileInfo {
    private final File file;
    public FileInfo(File file){
        this.file = file;
    }

    public String getName(){
        return file.getName();
    }

    public String getPath(){
        return encodePath(file.getAbsolutePath());
    }

    public long getSize(){
        return file.length();
    }

    public long getTime(){
        return file.lastModified();
    }

    public String getType(){
        if(file.isDirectory()){
            return "folder";
        }else{
            return "file";
        }
    }


    public static String encodePath(String path)  {
        return Base64.getEncoder().encodeToString(path.getBytes(StandardCharsets.UTF_8));
    }

    public static String decodePath(String path) {
        return new String(Base64.getDecoder().decode(path), StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(encodePath("G:\\movies\\afd.mkv"));
    }
}
