package se.lth.base.server.database;

import java.util.logging.Level;

/**
 * This is used to communicate to client about usage errors without exposing underlying implementation details.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public enum ErrorType {
    UNKNOWN(Level.SEVERE), DUPLICATE(Level.WARNING), NOT_FOUND(Level.WARNING), DATA_QUALITY(Level.WARNING);

    private final Level level;

    ErrorType(Level level) {
        this.level = level;
    }

    public Level logLevel() {
        return level;
    }
}
