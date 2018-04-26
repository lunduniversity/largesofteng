package se.lth.base.server;

/**
 * Contains configuration for server settings.
 * In a "real" application this would be a more sophisticated solution.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public class Config {
    public static int PORT = 8080;
    public static String DATABASE_DIR = "~/base-server";
    public static String DATABASE_DRIVER = "jdbc:h2:" + DATABASE_DIR + "/data";

    //public static String SERVER_HOST = "http://localhost:" + PORT;
    public static String SERVER_HOST = "localhost";
    public static String REST_PREFIX = "/rest";

    public static String USER_TOKEN_COOKIE = "USER_TOKEN";
}
