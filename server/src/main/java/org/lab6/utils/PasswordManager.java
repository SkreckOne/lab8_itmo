package org.lab6.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


public class PasswordManager {
    static private String pepper = "Wc532L%iYgTCft%@,gFm,+zv}3fTSU";
    static public String hashPassword(String password, String salt) {
        String strToHash = pepper + password + salt;
        byte[] hashedBytes = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            hashedBytes = md.digest(strToHash.getBytes(StandardCharsets.UTF_8));

        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.toString());
        }

        StringBuilder sb = new StringBuilder();
        if(hashedBytes != null) {
            for(byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
        }

        return sb.toString();
    }

    static public String generateSalt(int length){
        Random random = new Random();

        int leftLimit = 32;
        int rightLimit = 126;

        StringBuilder buffer = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }

        return buffer.toString();
    }
}
