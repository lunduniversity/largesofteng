package se.lth.base.server.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Helper to make working with JDBC's checked exception easier.
 * @param <T> Java type of mapped object.
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public interface Mapper<T> extends Function<ResultSet, T> {

    Function<ResultSet, Map<String, Object>> MAP_MAPPER = (Mapper<Map<String, Object>>) resultSet -> {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int c = metaData.getColumnCount();
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < c; i++) {
            map.put(metaData.getColumnLabel(i + 1), resultSet.getObject(i + 1));
        }
        return map;
    };

    @Override
    default T apply(ResultSet resultSet) {
        try {
            return map(resultSet);
        } catch (SQLException e) {
            throw new DataAccessException(e, ErrorType.UNKNOWN);
        }
    }

    T map(ResultSet resultSet) throws SQLException;
}
