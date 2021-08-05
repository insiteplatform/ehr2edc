package eu.ehr4cr.workbench.local.model.security;

public class UserObjectMother {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email@email.com";
    private static final boolean ENABLED = true;

    public static User aDefaultUser() {
        return new User(USERNAME, PASSWORD, EMAIL, ENABLED);
    }

    public static SystemUser aDefaultSystemUser() {
        return new SystemUser();
    }
}
