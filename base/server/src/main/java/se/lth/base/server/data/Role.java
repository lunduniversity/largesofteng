package se.lth.base.server.data;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents the capabilities of users in the system. A user can have only one role, however there is a hierarchy in
 * the roles, such that an admin can perform all user activities. This is implemented by the {@link #clearanceFor(Role)}
 * method.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public enum Role {

    NONE(1),
    USER(2),
    ADMIN(3);

    /**
     * The reason for this strange is to be able to use @{@link javax.annotation.security.RolesAllowed} which requires a
     * String, but at the same time we want to have typed Roles.
     */
    public static class Names {
        public static final String USER = "USER";
        public static final String ADMIN = "ADMIN";
    }

    private final int level;

    Role(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean clearanceFor(Role other) {
        return this.level >= other.level;
    }

    public static Set<Role> ALL_ROLES = new LinkedHashSet<>(Arrays.asList(USER, ADMIN));
}
