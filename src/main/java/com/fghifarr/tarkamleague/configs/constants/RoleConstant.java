package com.fghifarr.tarkamleague.configs.constants;

public class RoleConstant {
    public static final String ADMINISTRATOR = "ADMINISTRATOR";
    public static final String CREATOR = "CREATOR";
    public static final String EDITOR = "EDITOR";
    public static final String VIEWER = "VIEWER";

    public static final String HAS_ROLE_ADMINISTRATOR = "hasRole('"+ADMINISTRATOR+"')";
    public static final String HAS_ROLE_ADMINISTRATOR_CREATOR = "hasAnyRole('" +
            ADMINISTRATOR + "', '" +
            CREATOR + "')";
    public static final String HAS_ROLE_ADMINISTRATOR_EDITOR = "hasAnyRole('" +
            ADMINISTRATOR + "', '" +
            EDITOR + "')";
}
