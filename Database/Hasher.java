package Database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;

public class Hasher {
    public static String GenerateSalt() {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        // Convert the salt to a hexadecimal string
        StringBuilder hexSalt = new StringBuilder();
        for (byte b : salt) {
            hexSalt.append(String.format("%02x", b));
        }

        return hexSalt.toString();
    }

    public static String hashPassword(String password, String salt) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);

            // Create a MessageDigest instance for SHA-256 (you can choose a different algorithm)
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Add the salt to the message digest
            digest.update(saltBytes);

            // Get the hashed password bytes
            byte[] hashedPassword = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convert the hashed password bytes to hexadecimal format
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedPassword) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception appropriately
            e.printStackTrace();
            return null;
        }
    }
}
