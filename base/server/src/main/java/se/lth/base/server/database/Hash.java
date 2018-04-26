package se.lth.base.server.database;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Locale;

/**
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public class Hash {

    private static final int SIZE = 128;
    private static final int ITERATION_COST = 16;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    /**
     * Hash password using hashing algorithm intended for this purpose.
     *
     * @param salt     is added to password to protect from rainbow attacks.
     * @param phrase users password, with no assumptions on strength.
     * @return base64 encoded hash result.
     */
    public static String toBase64(String salt, String phrase) {
        try {
            KeySpec spec = new PBEKeySpec(phrase.toCharArray(),
                    salt.toLowerCase(Locale.ENGLISH).getBytes("UTF-8"), ITERATION_COST, SIZE);
            SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] blob = f.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(blob);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Missing algorithm: " + ALGORITHM, ex);
        } catch (InvalidKeySpecException ex) {
            throw new IllegalStateException("Invalid SecretKeyFactory", ex);
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("Invalid encoding", ex);
        }
    }

    public static void main(String[] args) {
        System.out.println(toBase64("Test", "password"));
        System.out.println(toBase64("Admin", "password"));
    }
}
