package se.lth.base.server.database;

import org.junit.Test;
import se.lth.base.server.database.Hash;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public class HashTest {
    @Test
    public void match() {
        String pwd = Hash.toBase64("a", "123");
        String m = Hash.toBase64("b", "123");
        assertNotEquals(pwd, m);
        assertEquals(pwd, Hash.toBase64("a", "123"));
    }
}
