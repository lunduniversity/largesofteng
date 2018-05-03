package se.lth.base.server.data;

import org.junit.Before;
import org.junit.Test;
import se.lth.base.server.database.DataAccessException;
import se.lth.base.server.database.BaseDataAccessTest;

import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public class SimpleDataAccessTest extends BaseDataAccessTest {

    private final SimpleDataAccess simpleDao = new SimpleDataAccess(IN_MEM_DRIVER_URL);
    private List<User> users;

    @Before
    public void addUsers() {
        UserDataAccess userStorage = new UserDataAccess(IN_MEM_DRIVER_URL);
        userStorage.addUser(new Credentials("user1", "p1", Role.USER));
        userStorage.addUser(new Credentials("user2", "p2", Role.USER));
        users = userStorage.getUsers().stream()
                .filter(u -> u.getName().equals("user1") || u.getName().equals("user2"))
                .collect(Collectors.toList());
    }

    @Test
    public void getNoSimple() {
        assertTrue(simpleDao.getAllSimple().isEmpty());
    }

    @Test(expected = DataAccessException.class)
    public void addToNoOne() {
        simpleDao.addSimple(-10, "meh");
    }

    @Test
    public void addToUser() {
        Simple data = simpleDao.addSimple(users.get(0).getId(), "user1s data");
        assertEquals(users.get(0).getId(), data.getUserId());
        assertEquals("user1s data", data.getPayload());
    }

    @Test
    public void getAllDataFromDifferentUsers() {
        int id1 = users.get(0).getId();
        int id2 = users.get(1).getId();
        simpleDao.addSimple(id1, "d1");
        assertEquals(1, simpleDao.getAllSimple().size());
        simpleDao.addSimple(id2, "d2");
        assertEquals(2, simpleDao.getAllSimple().size());
        assertEquals(2L, simpleDao.getAllSimple().stream().map(Simple::getUserId).distinct().count());
    }
}
