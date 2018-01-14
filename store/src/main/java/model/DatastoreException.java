package model;

public class DatastoreException extends Exception {

    public DatastoreException(String reason) {
        super(reason);
    }

    public DatastoreException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public DatastoreException(Throwable cause) {
        super(cause);
    }
}
