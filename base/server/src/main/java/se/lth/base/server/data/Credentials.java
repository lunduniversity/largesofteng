package se.lth.base.server.data;

import com.google.gson.annotations.Expose;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.UUID;

/**
 * Used for authentication and user operations requiring passwords.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public class Credentials {

    private final String username;
    @Expose(serialize = false)
    private final String password;
    private final Role role;

    public Credentials(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    // Password hashing function parameters.
    private static final int SIZE = 256;
    private static final int ITERATION_COST = 16;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    public boolean validPassword() {
        return this.password.length() >= 8;
    }

    public boolean hasPassword() {
        return password != null;
    }

    /**
     * Hash password using hashing algorithm intended for this purpose.
     *
     * @return base64 encoded hash result.
     */
    UUID generatePasswordHash(long salt) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(),
                    ByteBuffer.allocate(8).putLong(salt).array(),
                    ITERATION_COST, SIZE);
            SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] blob = f.generateSecret(spec).getEncoded();
            LongBuffer lb = ByteBuffer.wrap(blob).asLongBuffer();
            return new UUID(lb.get(), lb.get());
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Missing algorithm: " + ALGORITHM, ex);
        } catch (InvalidKeySpecException ex) {
            throw new IllegalStateException("Invalid SecretKeyFactory", ex);
        }
    }

    static long generateSalt() {
        return new SecureRandom().nextLong();
    }

    public static void main(String[] args) {
        long s1 = generateSalt();
        long s2 = generateSalt();
        System.out.println(s1);
        System.out.println(new Credentials("Admin", "password", Role.ADMIN).generatePasswordHash(s1));

        System.out.println(s2);
        System.out.println(new Credentials("Test", "password", Role.USER).generatePasswordHash(s2));
    }
}