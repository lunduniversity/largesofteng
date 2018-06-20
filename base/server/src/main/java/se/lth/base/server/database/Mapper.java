package se.lth.base.server.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Helper to make working with JDBC's checked exception easier.
 *
 * @param <T> Java type of mapped object.
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public interface Mapper<T> {
    T map(ResultSet resultSet) throws SQLException;
}
