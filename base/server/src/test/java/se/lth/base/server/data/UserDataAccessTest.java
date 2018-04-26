package se.lth.base.server.data;

import org.junit.Test;
import se.lth.base.server.database.DataAccessException;
import se.lth.base.server.database.DataAccessTest;

import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.*;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public class UserDataAccessTest extends DataAccessTest {

    private final UserDataAccess userStorage = new UserDataAccess(IN_MEM_DRIVER_URL);

    @Test
    public void addNewUser() {
        userStorage.addUser("Generic", UserRole.USER, "qwerty");
        List<User> users = userStorage.getUsers();
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Generic") && u.getRoles().contains(UserRole.USER)));
    }

    @Test(expected = DataAccessException.class)
    public void addDuplicatedUser() {
        userStorage.addUser("Gandalf", UserRole.USER, "mellon");
        userStorage.addUser("Gandalf", UserRole.USER, "vapenation");
    }

    @Test(expected = DataAccessException.class)
    public void addShortUser() {
        userStorage.addUser("Gry", UserRole.USER, "no");
    }

    @Test
    public void getUsersContainsAdmin() {
        assertTrue(userStorage.getUsers().stream().anyMatch(u -> u.getRoles().contains(UserRole.ADMIN)));
    }

    @Test
    public void removeNoUser() {
        assertFalse(userStorage.deleteUser(-1));
    }

    @Test
    public void removeUser() {
        User user = userStorage.addUser("Sven", UserRole.ADMIN, "a");
        assertTrue(userStorage.getUsers().stream().anyMatch(u -> u.getName().equals("Sven")));
        userStorage.deleteUser(user.getId());
        assertTrue(userStorage.getUsers().stream().noneMatch(u -> u.getName().equals("Sven")));
    }

    @Test(expected = DataAccessException.class)
    public void authenticateNoUser() {
        userStorage.authenticate("Waldo", "?");
    }

    @Test
    public void authenticateNewUser() {
        userStorage.addUser("Pelle", UserRole.USER, "!2");
        UserSession pellesSession = userStorage.authenticate("Pelle", "!2");
        assertEquals("Pelle", pellesSession.getUser().getName());
        assertNotNull(pellesSession.getSessionId());
    }

    @Test
    public void authenticateNewUserTwice() {
        userStorage.addUser("Elin", UserRole.USER, "password");

        UserSession authenticated = userStorage.authenticate("Elin", "password");
        assertNotNull(authenticated);
        assertEquals("Elin", authenticated.getUser().getName());

        UserSession authenticatedAgain = userStorage.authenticate("Elin", "password");
        assertNotEquals(authenticated.getSessionId(), authenticatedAgain.getSessionId());
    }

    @Test
    public void removeNoSession() {
        assertFalse(userStorage.removeSession(UUID.randomUUID()));
    }

    @Test
    public void removeSession() {
        userStorage.addUser("MormorElsa", UserRole.USER, "kanelbulle");
        UserSession session = userStorage.authenticate("MormorElsa", "kanelbulle");
        assertTrue(userStorage.removeSession(session.getSessionId()));
        assertFalse(userStorage.removeSession(session.getSessionId()));
    }

    @Test(expected = DataAccessException.class)
    public void failedAuthenticate() {
        userStorage.addUser("steffe", UserRole.USER, "kittylover1996!");
        userStorage.authenticate("steffe", "cantrememberwhatitwas! nooo!");
    }

    @Test
    public void checkSession() {
        userStorage.addUser("uffe", UserRole.ADMIN, "genius programmer");
        UserSession session = userStorage.authenticate("uffe", "genius programmer");
        UserSession checked = userStorage.getSession(session.getSessionId());
        assertEquals("uffe", checked.getUser().getName());
        assertEquals(session.getSessionId(), checked.getSessionId());
    }

    @Test
    public void checkRemovedSession() {
        userStorage.addUser("lisa", UserRole.ADMIN, "y");
        UserSession session = userStorage.authenticate("lisa", "y");
        UserSession checked = userStorage.getSession(session.getSessionId());
        assertEquals(session.getSessionId(), checked.getSessionId());
        userStorage.removeSession(checked.getSessionId());
        try {
            userStorage.getSession(checked.getSessionId());
            fail("Should not validate removed session");
        } catch (DataAccessException ignored) {
        }
    }
}
