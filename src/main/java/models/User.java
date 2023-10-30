package models;

/**
 * User model that stores the attributes of a user
 */
public class User {

    /**
     * stored username for this user
     */
    private final String username;

    /**
     * stored password for this user
     */
    private final String password;

    /**
     * stored email for this user
     */
    private final String email;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
