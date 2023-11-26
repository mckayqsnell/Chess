package responses;

/**
 * parent class for all responses
 * every response has a message and status code associated with it
 */
public abstract class ResponseParent {

    /**
     * the message associated with this response
     */
    private String message;

    public ResponseParent() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
