package webSocketMessages.serverMessages;

public class NotificationMessage extends ServerMessage {
    String message;

    public NotificationMessage(ServerMessageType type) {
        super(type);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
