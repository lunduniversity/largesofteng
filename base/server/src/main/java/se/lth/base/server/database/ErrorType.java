package se.lth.base.server.database;

import java.util.logging.Level;

/**
 * This is used to communicate to client about usage errors without exposing underlying implementation details.
 * It is not a one to one mapping between HTTP status codes and errors, but as close as possible.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public enum ErrorType {
    UNKNOWN(500, Level.SEVERE),
    BAD_MAPPING(500, Level.SEVERE),
    DUPLICATE(400, Level.WARNING),
    NOT_FOUND(404, Level.WARNING),
    DATA_QUALITY(400, Level.WARNING);

    private final Level level;
    private final int httpCode;

    ErrorType(int httpCode, Level level) {
        this.httpCode = httpCode;
        this.level = level;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public Level getLevel() {
        return level;
    }
}
