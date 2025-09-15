package com.greenloop.common.constant;


public final class PermissionConstants {

    // Permission Names
    public static final String USER_READ = "user:read";
    public static final String USER_WRITE = "user:write";
    public static final String ADMIN_READ = "admin:read";
    public static final String ADMIN_WRITE = "admin:write";

    // Permission Display Names
    public static final String USER_READ_DISPLAY = "Read User";
    public static final String USER_WRITE_DISPLAY = "Write User";
    public static final String ADMIN_READ_DISPLAY = "Admin Read";
    public static final String ADMIN_WRITE_DISPLAY = "Admin Write";

    // Resources
    public static final String USER_RESOURCE = "user";
    public static final String ADMIN_RESOURCE = "admin";

    // Actions
    public static final String READ_ACTION = "read";
    public static final String WRITE_ACTION = "write";
    public static final String DELETE_ACTION = "delete";
    public static final String CREATE_ACTION = "create";

    private PermissionConstants() {
    }
}
