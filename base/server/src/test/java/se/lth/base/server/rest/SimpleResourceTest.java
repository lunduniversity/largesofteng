package se.lth.base.server.rest;

import org.junit.Before;
import org.junit.Test;
import se.lth.base.server.BaseResourceTest;
import se.lth.base.server.data.Simple;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotEquals;

public class SimpleResourceTest extends BaseResourceTest {

    private static final GenericType<List<Simple>> SIMPLE_LIST = new GenericType<List<Simple>>() {
    };

    @Before
    public void loginTest() {
        login(TEST_CREDENTIALS);
    }

    @Test(expected = ForbiddenException.class)
    public void getSimpleAsNoOne() {
        logout();
        target("simple").request().get(SIMPLE_LIST);
    }

    @Test
    public void getNoSimpleData() {
        List<Simple> list = target("simple").request().get(SIMPLE_LIST);
        assertTrue(list.isEmpty());
    }

    @Test
    public void addSimple() {
        Simple simple = target("simple").request()
                .post(Entity.json(Collections.singletonMap("payload", "asdf")), Simple.class);
        assertEquals(TEST.getId(), simple.getUserId());
        assertEquals("asdf", simple.getPayload());
        assertNotEquals(0, simple.getId());
        assertNotEquals(0L, simple.getCreated());
    }

    @Test
    public void addManySimple() {
        for (int i = 0; i < 10; i++) {
            target("simple").request()
                    .post(Entity.json(Collections.singletonMap("payload", "asdf")));
        }
        List<Simple> list = target("simple").request().get(SIMPLE_LIST);
        assertEquals(10, list.size());
    }

    @Test(expected = ForbiddenException.class)
    public void getAllDataAsUser() {
        target("simple").path("all").request().get(SIMPLE_LIST);
    }

    @Test(expected = ForbiddenException.class)
    public void getSomeonesDataAsUser() {
        target("simple").path("10").request().get(SIMPLE_LIST);
    }

    @Test
    public void getAllDataAsAdmin() {
        // Post data as Test user
        target("simple").request()
                .post(Entity.json(Collections.singletonMap("payload", "tests")));

        // Post data as Admin user
        login(ADMIN_CREDENTIALS);
        target("simple").request().post(Entity.json(Collections.singletonMap("payload", "admins")));

        // There should be data from two users
        List<Simple> simples = target("simple").path("all").request().get(SIMPLE_LIST);
        assertEquals(2, simples.stream()
                .mapToInt(Simple::getUserId)
                .distinct()
                .count());
    }

    @Test
    public void getSomeonesDataAsAdmin() {
        // Post data as Test user
        target("simple").request().post(Entity.json(Collections.singletonMap("payload", "tests")));


        // Get Test's data as Admin
        login(ADMIN_CREDENTIALS);
        List<Simple> testsSimples = target("simple")
                .path(Integer.toString(TEST.getId()))
                .request()
                .get(SIMPLE_LIST);
        assertEquals("tests", testsSimples.get(0).getPayload());
    }
}
