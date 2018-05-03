package se.lth.base.server.database;

import org.junit.After;
import org.junit.Before;

import java.sql.SQLException;

/**
 * Base class for H2 database tests. The connection url configures an in-memory database.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public abstract class BaseDataAccessTest {

    public static final String IN_MEM_DRIVER_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private final CreateSchema createSchema = new CreateSchema(IN_MEM_DRIVER_URL);

    @Before
    public void createDatabase() throws SQLException {
        createSchema.createSchema();
    }

    @After
    public void deleteDatabase() throws SQLException {
        createSchema.dropAll();
    }
}
