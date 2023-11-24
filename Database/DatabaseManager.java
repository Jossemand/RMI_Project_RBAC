package Database;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class DatabaseManager {



    public static boolean findUsernameInFile(String filename, String stringInput) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains(stringInput)) {
                return true;
            }
        }
        reader.close();
        return false;
    }

    Hasher hasher = new Hasher();
    public boolean AddUserToFile(String un, String pw) throws IOException {
        if(findUsernameInFile("PublicFileUsers.txt",un)) {
            // The user already exists in the file
            return false;
        } else {
            // Add the user to the file
            try {
                String salt = hasher.GenerateSalt();
                String hashedPw = Hasher.hashPassword(pw, salt);
                System.out.println("Adding user: " + un + " to File");
                FileWriter writer = new FileWriter("PublicFileUsers.txt", true);
                writer.write(un + ":" + hashedPw + ":" + salt + "\r\n");

                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public boolean Login(String username, String password) {
        String[] credentials = findUserCredentials(username, "PublicFileUsers.txt");
        if (credentials != null) {
            String storedUsername = credentials[0];
            String storedPasswordHash = credentials[1];
            String storedSalt = credentials[2];
            String hashedPassword = Hasher.hashPassword(password, storedSalt);

            return storedUsername.equals(username) && storedPasswordHash.equals(hashedPassword);
        }
        return false;
    }

    public static String[] findUserCredentials(String username, String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) { // Close parenthesis here
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into parts using ":"
                String[] parts = line.split(":");
                if (parts.length == 4) {
                    String storedUsername = parts[0];
                    if (storedUsername.equals(username)) {
                        String storedPassword = parts[1];
                        String salt = parts[2];
                        String role = parts[3];
                        return new String[]{storedUsername, storedPassword, salt, role};
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Return null if the username is not found
        return null;
    }
}


