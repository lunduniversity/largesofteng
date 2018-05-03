package se.lth.base.server.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CredentialsTest {
    @Test
    public void saltApplied() {
        Credentials a = new Credentials("a", "123", Role.NONE);
        Credentials b = new Credentials("b", "123", Role.NONE);
        String pwd = a.generatePasswordHash(1L);
        String m = b.generatePasswordHash(2L);
        assertNotEquals(pwd, m);
        assertEquals(pwd, a.generatePasswordHash(1L));
    }
}
