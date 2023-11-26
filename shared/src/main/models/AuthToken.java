package models;

import java.util.Objects;
import java.util.UUID;

/**
 * authToken model that stores the authToken string and username associated with it
 */
public class AuthToken {

    private String authToken;

    private String username;

    public AuthToken() {
    }

    public AuthToken(String username) {
        this.username = username;
        authToken = UUID.randomUUID().toString();
    }

    public AuthToken(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return String.format("Username: %s, AuthToken: %s", username, authToken);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthToken authToken1 = (AuthToken) o;
        return Objects.equals(authToken, authToken1.authToken) && Objects.equals(username, authToken1.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, username);
    }
}
