package models;

import java.util.UUID;

/**
 * authToken model that stores the authToken string and username associated with it
 */
public class AuthToken {

    private final String authToken;

    private final String username;

    public AuthToken(String username) {
        this.username = username;
        authToken = UUID.randomUUID().toString();
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }
}
