package se.lth.base.server.rest;

import org.junit.Before;
import org.junit.Test;
import se.lth.base.server.BaseResourceTest;
import se.lth.base.server.data.Foo;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotEquals;

public class FooResourceTest extends BaseResourceTest {

    private static final GenericType<List<Foo>> FOO_LIST = new GenericType<List<Foo>>() {
    };

    @Before
    public void loginTest() {
        login(TEST_CREDENTIALS);
    }

    @Test(expected = ForbiddenException.class)
    public void getFooAsNoOne() {
        logout();
        target("foo").request().get(FOO_LIST);
    }

    @Test
    public void getNoFooData() {
        List<Foo> list = target("foo").request().get(FOO_LIST);
        assertTrue(list.isEmpty());
    }

    @Test
    public void addFoo() {
        Foo foo = target("foo").request()
                .post(Entity.json(Collections.singletonMap("payload", "asdf")), Foo.class);
        assertEquals(TEST.getId(), foo.getUserId());
        assertEquals("asdf", foo.getPayload());
        assertNotEquals(0, foo.getId());
        assertNotEquals(0L, foo.getCreated());
    }

    @Test
    public void addManyFoo() {
        for (int i = 0; i < 10; i++) {
            target("foo").request()
                    .post(Entity.json(Collections.singletonMap("payload", "asdf")));
        }
        List<Foo> list = target("foo").request().get(FOO_LIST);
        assertEquals(10, list.size());
    }

    @Test(expected = ForbiddenException.class)
    public void getAllDataAsUser() {
        target("foo").path("all").request().get(FOO_LIST);
    }

    @Test(expected = ForbiddenException.class)
    public void getSomeonesDataAsUser() {
        target("foo").path("user").path("10").request().get(FOO_LIST);
    }

    @Test
    public void getAllDataAsAdmin() {
        // Post data as Test user
        target("foo").request()
                .post(Entity.json(Collections.singletonMap("payload", "tests")));

        // Post data as Admin user
        login(ADMIN_CREDENTIALS);
        target("foo").request().post(Entity.json(Collections.singletonMap("payload", "admins")));

        // There should be data from two users
        List<Foo> foos = target("foo").path("all").request().get(FOO_LIST);
        assertEquals(2, foos.stream()
                .mapToInt(Foo::getUserId)
                .distinct()
                .count());
    }

    @Test
    public void getSomeonesDataAsAdmin() {
        // Post data as Test user
        target("foo").request().post(Entity.json(Collections.singletonMap("payload", "tests")));

        // Get Test's data as Admin
        login(ADMIN_CREDENTIALS);
        List<Foo> testsFoos = target("foo")
                .path("user")
                .path(Integer.toString(TEST.getId()))
                .request()
                .get(FOO_LIST);
        assertEquals("tests", testsFoos.get(0).getPayload());
    }
}
