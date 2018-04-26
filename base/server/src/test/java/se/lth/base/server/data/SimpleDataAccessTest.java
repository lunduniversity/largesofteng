package se.lth.base.server.data;

import org.junit.Before;
import org.junit.Test;
import se.lth.base.server.database.DataAccessException;
import se.lth.base.server.database.DataAccessTest;

import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public class SimpleDataAccessTest extends DataAccessTest {

    private final SimpleDataAccess simpleStorage = new SimpleDataAccess(IN_MEM_DRIVER_URL);
    private List<User> users;

    @Before
    public void addUsers() {
        UserDataAccess userStorage = new UserDataAccess(IN_MEM_DRIVER_URL);
        userStorage.addUser("user1", UserRole.USER, "p1");
        userStorage.addUser("user2", UserRole.USER, "p2");
        users = userStorage.getUsers().stream()
                .filter(u -> u.getName().equals("user1") || u.getName().equals("user2"))
                .collect(Collectors.toList());
    }

    @Test
    public void getNoData() {
        assertTrue(simpleStorage.getAllData().isEmpty());
    }

    @Test(expected = DataAccessException.class)
    public void addToNoOne() {
        simpleStorage.addData(-10, "meh");
    }

    @Test
    public void addToUser() {
        SimpleData data = simpleStorage.addData(users.get(0).getId(), "user1s data");
        assertEquals(users.get(0).getId(), data.getUserId());
        assertEquals("user1s data", data.getData());
    }

    @Test
    public void getAllDataFromDifferentUsers() {
        int id1 = users.get(0).getId();
        int id2 = users.get(1).getId();
        simpleStorage.addData(id1, "d1");
        assertEquals(1, simpleStorage.getAllData().size());
        simpleStorage.addData(id2, "d2");
        assertEquals(2, simpleStorage.getAllData().size());
        assertEquals(2L, simpleStorage.getAllData().stream().map(SimpleData::getUserId).distinct().count());
    }
}
