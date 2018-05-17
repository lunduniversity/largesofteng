package se.lth.base.server.data;

import org.junit.Before;
import org.junit.Test;
import se.lth.base.server.Config;
import se.lth.base.server.database.DataAccess;
import se.lth.base.server.database.DataAccessException;
import se.lth.base.server.database.BaseDataAccessTest;

import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

    @Test
    public void deleteFoo() {
        Foo foo = fooDao.addFoo(TEST.getId(), "data");
        assertTrue(fooDao.deleteFoo(foo.getId(), TEST.getId()));
        assertTrue(fooDao.getUsersFoo(TEST.getId()).isEmpty());
    }

    @Test
    public void dontDeleteOthersFoo() {
        Foo foo = fooDao.addFoo(ADMIN.getId(), "data");
        assertFalse(fooDao.deleteFoo(foo.getId(), TEST.getId()));
    }

    @Test
    public void deleteMissingFoo() {
        assertFalse(fooDao.deleteFoo(1, 1));
    }

    @Test
    public void updateFooTotal() {
        Foo foo = fooDao.addFoo(TEST.getId(), "data");
        fooDao.updateTotal(foo.getId(), TEST.getId(), 1);
        fooDao.updateTotal(foo.getId(), TEST.getId(), 1);
        fooDao.updateTotal(foo.getId(), TEST.getId(), -1);
        assertEquals(2, fooDao.getUsersFoo(TEST.getId()).get(0).getTotal());
    }

    @Test(expected = DataAccessException.class)
    public void updateMissingFoo() {
        fooDao.updateTotal(1, 1, 1);
    }

    @Test(expected = DataAccessException.class)
    public void dontUpdateOthersFoo() {
        Foo foo = fooDao.addFoo(ADMIN.getId(), "admin");
        fooDao.updateTotal(foo.getId(), TEST.getId(), 1);
    }

    @Test(expected = DataAccessException.class)
    public void updateToZero() {
        Foo foo = fooDao.addFoo(ADMIN.getId(), "will go to zero");
        fooDao.updateTotal(foo.getId(), ADMIN.getId(), -1);
    }
}
