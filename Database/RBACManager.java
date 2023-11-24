package Database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RBACManager {
    final static String filePathAccess = "PublicFileAccessControl.txt";
    final static String filepathUsers = "PublicFileUsersOriginal.txt";
    public static boolean findUserAccess(String username, String method) {
        String role = findUserRole(username);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePathAccess))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into parts using ":"
                String[] parts = line.split(":");
                if(role.equals(parts[0]))
                    for (String part : parts) {
                        if (part.equals(method)) {
                            return true;
                        }
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Return null if the username is not found
        return false;
    }

    private static String findUserRole(String username) {
        String[]  user = DatabaseManager.findUserCredentials(username, filepathUsers);

        return user[3];
    }

}
