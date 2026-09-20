package src;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
 
public class SHA {
 
    public static String hash(String input) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        }
        catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}