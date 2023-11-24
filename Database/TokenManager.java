package Database;
import java.time.LocalTime;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.time.temporal.ChronoUnit;
public class TokenManager {
    HashMap<String, LocalTime> tokens = new HashMap<String, LocalTime>();

    HashMap<String, String> tokensUser = new HashMap<String, String>();
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    public void AddToken (String token, String username) {
        tokens.put(token, LocalTime.now());
        tokensUser.put(token, username);
    }
    public String GenerateToken () {
        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public boolean ConfirmToken(String token) {
        if(tokens.containsKey(token)) {
            LocalTime timeOfSession = tokens.get(token);
            LocalTime currentTime = LocalTime.now();
            long minutesDifference = ChronoUnit.MINUTES.between(timeOfSession, currentTime);
            return minutesDifference < 5;
        } else {
            return false;
        }

    }
    public String getUsernameFromToken(String token) {
        if (tokensUser.containsKey(token)) {

            return tokensUser.get(token);
        } else {
            return null;
        }
    }

}
