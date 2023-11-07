package models;

import java.util.Objects;

/**
 * User model that stores the attributes of a user
 */
public class User {
    private String username;
    private String password;
    private String email;

    public User() {
    }

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

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user1 = (User) o;
        return Objects.equals(username, user1.username) && Objects.equals(password, user1.password)
                && Objects.equals(email, user1.email);
    }

    @Override
    public String toString() {
        return String.format("Username: %s, Password: %s, Email: %s", username, password, email);
    }
}
