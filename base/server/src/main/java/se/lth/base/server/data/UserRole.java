package se.lth.base.server.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UserRole {

    public static final String ADMIN = "Admin";
    public static final String USER = "User";
    public static final String NONE = "None";

    public static final Set<String> ALL_ROLES = new HashSet<>(Arrays.asList(ADMIN, USER));

}
