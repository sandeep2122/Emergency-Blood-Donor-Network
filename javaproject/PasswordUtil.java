import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;
public class PasswordUtil {
    private static final int ITERATIONS = 600000;
    private static final int KEY_LENGTH = 256;
    public static String hashPassword(char[] password) throws Exception {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        PBEKeySpec spec = new PBEKeySpec( password, salt, ITERATIONS, KEY_LENGTH );
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA256" );
            byte[] hash = factory .generateSecret(spec) .getEncoded();
            return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
        }
        finally {
            spec.clearPassword();
        }
    }
    public static boolean verifyPassword( char[] password, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expected = Base64.getDecoder().decode(parts[1]);
            PBEKeySpec spec = new PBEKeySpec( password, salt, ITERATIONS, KEY_LENGTH );
            try {
                SecretKeyFactory factory = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA256" );
                byte[] actual = factory .generateSecret(spec) .getEncoded();
                boolean matches = MessageDigest.isEqual( actual, expected );
                Arrays.fill(actual, (byte) 0);
                return matches;
            }
            finally {
                spec.clearPassword();
            }
        }
        catch (Exception e) {
            return false;
        }
    }
}
