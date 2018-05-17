package se.lth.base.server.data;

import org.junit.Test;
import se.lth.base.server.Config;
import se.lth.base.server.database.BaseDataAccessTest;
import se.lth.base.server.database.DataAccessException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public class FooDataAccessTest extends BaseDataAccessTest {

    private FooDataAccess fooDao = new FooDataAccess(Config.instance().getDatabaseDriver());

    @Test
    public void getNoFoo() {
        assertTrue(fooDao.getAllFoo().isEmpty());
    }

    @Test(expected = DataAccessException.class)
    public void addToNoOne() {
        fooDao.addFoo(-10, "meh");
    }

    @Test
    public void addToUser() {
        Foo data = fooDao.addFoo(TEST.getId(), "user1s data");
        assertEquals(TEST.getId(), data.getUserId());
        assertEquals("user1s data", data.getPayload());
    }

    @Test
    public void getAllDataFromDifferentUsers() {
        fooDao.addFoo(TEST.getId(), "d1");
        assertEquals(1, fooDao.getAllFoo().size());
        fooDao.addFoo(ADMIN.getId(), "d2");
        assertEquals(2, fooDao.getAllFoo().size());
        assertEquals(2L, fooDao.getAllFoo().stream().map(Foo::getUserId).distinct().count());
    }
}
