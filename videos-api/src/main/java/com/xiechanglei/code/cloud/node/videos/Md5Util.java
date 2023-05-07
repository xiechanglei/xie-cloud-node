package com.xiechanglei.code.cloud.node.videos;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
    public static String md5(String path) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(path.getBytes());
        byte[] bytes = md5.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
