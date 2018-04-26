package se.lth.base.server.data;

public class SimpleData {

    private final int id;
    private final int userId;
    private final String payload;
    private final long created;
    private final int count = 3;

    public SimpleData(int id, int userId, String payload, long created) {
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

    public String getData() {
        return payload;
    }

    public long getCreated() {
        return created;
    }
}


