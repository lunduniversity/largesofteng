package se.lth.base.server.database;

import org.h2.tools.RunScript;
import se.lth.base.server.Config;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

public class CreateSchema extends DataAccess {

    public CreateSchema(String driverUrl) {
        super(driverUrl);
    }

    public static void main(String[] args) throws Exception {
        CreateSchema cs = new CreateSchema(Config.DATABASE_DRIVER);
        cs.dropAll();
        cs.createSchema();
    }

    public void dropAll() {
        execute("DROP ALL OBJECTS");
    }

    public void createSchema() {
        try (Connection conn = getConnection()) {
            runScript(conn);
        } catch (SQLException e) {
            throw new DataAccessException(e, ErrorType.UNKNOWN);
        }
    }


    public boolean createSchemaIfNotExists() {
        boolean tableExists = query("SELECT COUNT(*) AS COUNT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'USER'")
                .map((Mapper<Long>) rs -> rs.getLong(1)).findFirst().orElseGet(() -> 0L) > 0;
        if (!tableExists) {
            createSchema();
        }
        return tableExists;
    }

    private static void runScript(Connection conn) throws SQLException {
        RunScript.execute(conn, new InputStreamReader(DataAccess.class.getResourceAsStream("schema.sql")));
    }
}
