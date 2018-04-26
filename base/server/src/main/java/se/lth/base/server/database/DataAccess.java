package se.lth.base.server.database;

import org.h2.api.ErrorCode;

import java.sql.*;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Base class for H2 database connections. Contains helper methods for common JDBC use cases
 * Also contains a method for setting up the database schema.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 * @see CreateSchema#main(String[])
 */
public abstract class DataAccess {

    private final String driverUrl;


    protected DataAccess(String driverUrl) {
        this.driverUrl = driverUrl;
    }

    Connection getConnection() throws SQLException {
        return DriverManager.getConnection(driverUrl, "sa", "");
    }

    protected int execute(String sql, Object... objects) {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql);) {
            for (int i = 0; i < objects.length; i++) {
                statement.setObject(i + 1, objects[i]);
            }
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw toException(e, e.getErrorCode());
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T insert(String sql, Object... objects) {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            for (int i = 0; i < objects.length; i++) {
                statement.setObject(i + 1, objects[i]);
            }
            statement.execute();
            try (ResultSet keys = statement.getGeneratedKeys();) {
                keys.next();
                return (T) keys.getObject(1);
            }
        } catch (SQLException e) {
            throw toException(e, e.getErrorCode());
        }
    }

    protected Stream<ResultSet> query(String sql, Object... objects) {
        UncheckedCloseable close = null;
        try {
            Connection connection = getConnection();
            close = UncheckedCloseable.wrap(connection);
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++) {
                statement.setObject(i + 1, objects[i]);
            }
            close = close.nest(statement);
            connection.setAutoCommit(false);
            ResultSet resultSet = statement.executeQuery();
            close = close.nest(resultSet);
            return StreamSupport.stream(new Spliterators.AbstractSpliterator<ResultSet>(
                    Long.MAX_VALUE, Spliterator.ORDERED) {
                @Override
                public boolean tryAdvance(Consumer<? super ResultSet> action) {
                    try {
                        if (!resultSet.next()) return false;
                        action.accept(resultSet);
                        return true;
                    } catch (SQLException e) {
                        throw toException(e, e.getErrorCode());
                    }
                }
            }, false).onClose(close);
        } catch (SQLException e) {
            if (close != null)
                try {
                    close.close();
                } catch (Exception ex) {
                    e.addSuppressed(ex);
                }
            throw toException(e, e.getErrorCode());
        }
    }

    private DataAccessException toException(Exception cause, int h2Code) {
        switch (h2Code) {
            case ErrorCode.REFERENTIAL_INTEGRITY_VIOLATED_PARENT_MISSING_1:
                return new DataAccessException("Resource not found", cause, ErrorType.NOT_FOUND);
            case ErrorCode.DUPLICATE_KEY_1:
                return new DataAccessException("Resource already exists", cause, ErrorType.DUPLICATE);
            case ErrorCode.CHECK_CONSTRAINT_VIOLATED_1:
                return new DataAccessException("Resource constraints violated", cause, ErrorType.DATA_QUALITY);
            default:
                return new DataAccessException(cause, ErrorType.UNKNOWN);
        }
    }

    private interface UncheckedCloseable extends Runnable, AutoCloseable {
        default void run() {
            try {
                close();
            } catch (Exception e) {
                throw new DataAccessException(e, ErrorType.UNKNOWN);
            }
        }

        static UncheckedCloseable wrap(AutoCloseable c) {
            return c::close;
        }

        default UncheckedCloseable nest(AutoCloseable c) {
            return () -> {
                try (UncheckedCloseable c1 = this) {
                    c.close();
                }
            };
        }
    }
}
