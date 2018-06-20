package se.lth.base.server.database;

import org.h2.tools.RunScript;
import se.lth.base.server.Config;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Contains helpers for creating the database schema. Each time the server starts the @{@link #createSchemaIfNotExists()}
 * method is called.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public class CreateSchema {

    private final String driverUrl;

    public CreateSchema(String driverUrl) {
        this.driverUrl = driverUrl;
    }

    public static void main(String[] args) throws Exception {
        CreateSchema cs = new CreateSchema(Config.instance().getDatabaseDriver());
        cs.dropAll();
        cs.createSchema();
    }

    public void dropAll() {
        new DataAccess<>(driverUrl, null).execute("DROP ALL OBJECTS");
    }

    public void createSchema() {
        try (Connection conn = new DataAccess<>(driverUrl, null).getConnection()) {
            runScript(conn);
        } catch (SQLException e) {
            throw new DataAccessException(e, ErrorType.UNKNOWN);
        }
    }

    public boolean createSchemaIfNotExists() {
        DataAccess<Long> counter = new DataAccess<>(driverUrl, (rs) -> rs.getLong(1));
        boolean tableExists = counter.queryFirst("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'USER'") > 0L;
        if (!tableExists) {
            createSchema();
            return true;
        }
        return false;
    }

    private static void runScript(Connection conn) throws SQLException {
        RunScript.execute(conn, new InputStreamReader(DataAccess.class.getResourceAsStream("schema.sql")));
    }
}
