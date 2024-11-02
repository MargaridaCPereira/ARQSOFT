package pt.psoft.g1.psoftg1.idgeneratormanagement;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class IdGenerator {

    private IdGenerator() {
        throw new UnsupportedOperationException("Cannot instantiate IdGenerator");
    }

    public static String generateHexadecimalId() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[12];
        secureRandom.nextBytes(randomBytes);

        StringBuilder hexString = new StringBuilder();
        for (byte b : randomBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String generateAlphanumericId() {
        long timestamp = System.currentTimeMillis();
        return hashToAlphanumeric(String.valueOf(timestamp));
    }

    private static String hashToAlphanumeric(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());

            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            StringBuilder alphanumericString = new StringBuilder();

            for (byte b : hash) {
                int index = Byte.toUnsignedInt(b) % chars.length();
                alphanumericString.append(chars.charAt(index));
            }

            return alphanumericString.substring(0, 20);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }
}