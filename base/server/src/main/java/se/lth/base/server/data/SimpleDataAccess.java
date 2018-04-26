package se.lth.base.server.data;

import se.lth.base.server.database.DataAccess;
import se.lth.base.server.database.Mapper;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SimpleDataAccess is a place-holder that shows how the base system can be extended with additional functionality.
 * <p>
 * Note that the name 'SimpleDataAccess' is not very good, since it is generic. In your project you are expected to give
 * more specific names to your classes, see for example @{@link UserDataAccess} which is more aptly named.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 * @see UserDataAccess
 * @see DataAccess
 */
public class SimpleDataAccess extends DataAccess {

    public SimpleDataAccess(String driverUrl) {
        super(driverUrl);
    }

    private final Mapper<SimpleData> RECORD_MAPPER = resultSet -> new SimpleData(
            resultSet.getInt("simple_id"),
            resultSet.getInt("user_id"),
            resultSet.getString("payload"),
            resultSet.getObject("created", Date.class).getTime());

    /**
     * Add new simple payload connected to a user.
     *
     * @param userId user to add payload to.
     * @param payload   new payload to append.
     */
    public SimpleData addData(int userId, String payload) {
        long created = System.currentTimeMillis();
        int simpleId = insert("INSERT INTO simple (user_id, payload, created) VALUES (?,?,?)",
                userId, payload, new Date(created));
        return new SimpleData(simpleId, userId, payload, created);
    }

    /**
     * @return all simple payload for all users.
     */
    public List<SimpleData> getAllData() {
        return query("SELECT * FROM simple").map(RECORD_MAPPER).collect(Collectors.toList());
    }

    /**
     * Get all simple payload created by a user.
     *
     * @param userId user to filter on.
     * @return users simple payload.
     */
    public List<SimpleData> getUsersData(int userId) {
        return query("SELECT * FROM simple WHERE user_id = ?", userId).map(RECORD_MAPPER).collect(Collectors.toList());
    }
}
