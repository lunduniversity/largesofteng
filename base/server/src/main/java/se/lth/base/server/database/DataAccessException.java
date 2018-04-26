package se.lth.base.server.database;

/**
 * Wraps SQLException and adds types @{@link ErrorType} to handled exceptions.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public class DataAccessException extends RuntimeException {

    private final ErrorType errorType;

    public DataAccessException(ErrorType errorType) {
        super(errorType.toString());
        this.errorType = errorType;
    }

    public DataAccessException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public DataAccessException(String message, Throwable cause, ErrorType errorType) {
        super(message, cause);
        this.errorType = errorType;
    }

    public DataAccessException(Throwable cause, ErrorType errorType) {
        super(errorType.toString(), cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
