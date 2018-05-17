package se.lth.base.server.data;

/**
 * Data class for the starting functionality.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 * @see FooDataAccess
 */
public class Foo {

    private final int id;
    private final int userId;
    private final String payload;
    private final long created;
    private int total;

    public Foo(int id, int userId, String payload, long created, int total) {
        this.id = id;
        this.userId = userId;
        this.payload = payload;
        this.created = created;
        this.total = total;
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

    public int getTotal() {
        return total;
    }
}


