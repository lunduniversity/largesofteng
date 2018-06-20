package se.lth.base.server.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is a convenience class which is useful for debugging SQL queries without having to write a fixed Mapper.
 */
public class MapMapper implements Mapper<Map<String, Object>> {

    @Override
    public Map<String, Object> map(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int c = metaData.getColumnCount();
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < c; i++) {
            map.put(metaData.getColumnLabel(i + 1), resultSet.getObject(i + 1));
        }
        return map;
    }
}
