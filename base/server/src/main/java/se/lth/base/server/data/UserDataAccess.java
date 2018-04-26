package se.lth.base.server.data;

import se.lth.base.server.database.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Basic functionality to support standard user operations. Some notable omissions are removing user, time out on
 * sessions, getting a user by name or id, etc.
 * <p>
 * This is intended to be as realistic as possible with reasonable security (single factor authentication).
 * The security measures are as follows.
 * <ul>
 * <li>All passwords are stored in a hashed format in the database, using @{@link Hash#toBase64(String, String)}.</li>
 * <li>Usernames are used to salt passwords,
 * <a href="https://en.wikipedia.org/wiki/Salt_(cryptography)">see here for explanation.</a>
 * <li>When a user does login, it receives a UUID-token. This token is then used to authenticate,
 * using @{@link #getSession}.
 * </li>
 * </ul>
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 * @see DataAccess
 */
public class UserDataAccess extends DataAccess {

    public UserDataAccess(String driverUrl) {
        super(driverUrl);
    }

    private final Mapper<User> USER_MAPPER = resultSet ->
            new User(resultSet.getInt("user_id"),
                    resultSet.getString("role"),
                    resultSet.getString("username"));

    /**
     * Add a new user to the system.
     *
     * @param username new users name, must be at least 4 characters long.
     * @param role     role of user.
     * @param password plain text password, will be stored in hashed form.
     * @throws DataAccessException if duplicated username or too short user names.
     */
    public User addUser(String username, String role, String password) {
        int userId = insert("INSERT INTO user (role_id, username, password_hash) VALUES ((" +
                        "SELECT role_id FROM user_role WHERE user_role.role=?),?,?)",
                role, username, Hash.toBase64(username, password));
        return new User(userId, role, username);
    }

    public User getUser(int userId) {
        return query("SELECT (user_id, role, username) FROM user WHERE user_id = ? " +
                "JOIN user_role ON user.role_id = user_role.role_id", userId)
                .map(USER_MAPPER)
                .findFirst()
                .orElseThrow(() -> new DataAccessException("User not found", ErrorType.NOT_FOUND));
    }

    public boolean deleteUser(int userId) {
        return execute("DELETE FROM user WHERE user_id = ?", userId) > 0;
    }

    /**
     * @return all users in the system.
     */
    public List<User> getUsers() {
        return query("SELECT user_id, username, role FROM user " +
                "JOIN user_role ON user.role_id = user_role.role_id").map(USER_MAPPER).collect(Collectors.toList());
    }

    /**
     * Fetch session and the corresponding user.
     *
     * @param sessionId globally unqiue identifier, stored in the client.
     * @return session object wrapping the user.
     * @throws DataAccessException if the session is not found.
     */
    public UserSession getSession(UUID sessionId) {
        User user = query("SELECT user.user_id, username, role FROM user " +
                "JOIN user_role ON user_role.role_id = user.role_id " +
                "JOIN session ON session.user_id = user.user_id " +
                "WHERE session.session_uuid = ?", sessionId)
                .map(USER_MAPPER)
                .findFirst()
                .orElseThrow(() -> new DataAccessException("Session not found", ErrorType.DATA_QUALITY));
        execute("UPDATE session SET last_seen = CURRENT_TIMESTAMP() " +
                "WHERE session_uuid = ?", sessionId);
        return new UserSession(sessionId, user);
    }

    /**
     * Logout a user. This method is idempotent, meaning it is safe to repeat indefinitely.
     *
     * @param sessionId session to remove
     * @return true if the session was found, false otherwise.
     */
    public boolean removeSession(UUID sessionId) {
        return execute("DELETE FROM session WHERE session_uuid = ?", sessionId) > 0;
    }

    /**
     * Login a user.
     *
     * @param username user name, it must also match alongside the password.
     * @param password plain text password.
     * @return New user session, consisting of a @{@link UUID} and @{@link User}.
     * @throws DataAccessException if the username or password does not match.
     */
    public UserSession authenticate(String username, String password) {
        String hash = Hash.toBase64(username, password);
        User user = query("SELECT user_id, username, role FROM user " +
                "JOIN user_role ON user.role_id = user_role.role_id " +
                "WHERE username = ? AND password_hash = ?", username, hash)
                .map(USER_MAPPER)
                .findFirst()
                .orElseThrow(() -> new DataAccessException("Username or password incorrect", ErrorType.DATA_QUALITY));
        UUID sessionId = insert("INSERT INTO session (user_id) " +
                "SELECT user_id from USER WHERE username = ?", user.getName());
        return new UserSession(sessionId, user);
    }
}
