package responses;

/**
 * response for a register request
 */
public class RegisterResponse extends ResponseParent {

    /**
     * the userName associated with this registerResponse
     */
    private String username;

    /**
     * the authToken associated with this registerResponse
     */
    private String authToken;

    public RegisterResponse() {
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
