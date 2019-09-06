package se.lth.base.server;

/**
 * Contains configuration for server settings.
 * In a "real" application this would be a more sophisticated solution, the config parameters should come from a file
 * or something that is actually possible to configure without recompiling the application.
 * There should also be support for error checking.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public class Config {

    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_DATABASE_DRIVER = "jdbc:h2:~/base-server/data";

    private static final Config INSTANCE = new Config(
            DEFAULT_PORT, DEFAULT_DATABASE_DRIVER
    );

    private String databaseDriver;
    private int port;

    private Config(int port, String databaseDriver) {
        this.port = port;
        this.databaseDriver = databaseDriver;
    }

    public static Config instance() {
        return INSTANCE;
    }

    public int getPort() {
        return port;
    }

    public String getDatabaseDriver() {
        return databaseDriver;
    }

    public void setDatabaseDriver(String databaseDriver) {
        this.databaseDriver = databaseDriver;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
