package se.lth.base.server.data;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

public class User implements Principal {

    public static User NONE = new User(0, UserRole.NONE, "-");

    private final int id;
    private final String role;
    private final String username;

    public User(int id, String role, String username) {
        this.id = id;
        this.role = role;
        this.username = username;
    }

    public Set<String> getRoles() {
        if (role.equals(UserRole.ADMIN)) {
            return UserRole.ALL_ROLES;
        }
        return Collections.singleton(role);
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public String getName() {
        return username;
    }
}
