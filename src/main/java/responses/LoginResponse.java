package responses;

/**
 * response for a login request
 */
public class LoginResponse extends ResponseParent {

    /**
     * the userName associated with this loginResponse
     */
    private String username;

    /**
     * the authToken associated with this loginResponse
     */
    private String authToken;

    public LoginResponse() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

}
