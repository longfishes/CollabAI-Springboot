package com.longfish.collabai.util;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;

public class MD5Util {

    public static String gen(Object obj) {
        if (obj == null) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            Class<?> clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();
            Arrays.sort(fields, Comparator.comparing(Field::getName));

            StringBuilder sb = new StringBuilder();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);
                if (value != null) {
                    sb.append(field.getName()).append("=").append(value).append(";");
                }
            }

            byte[] hash = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException | IllegalAccessException e) {
            throw new RuntimeException("MD5 calculation failed", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
