package se.lth.base.server.data;

/**
 * Data class for the starting functionality.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 * @see SimpleDataAccess
 */
public class Simple {

    private final int id;
    private final int userId;
    private final String payload;
    private final long created;
    private int count = 3;

    public Simple(int id, int userId, String payload, long created) {
        this.id = id;
        this.userId = userId;
        this.payload = payload;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getPayload() {
        return payload;
    }

    public long getCreated() {
        return created;
    }
}


