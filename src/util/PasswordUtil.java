package util;

import java.security.MessageDigest;

public class PasswordUtil {

    public static String maHoaMD5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] digest = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();

            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}