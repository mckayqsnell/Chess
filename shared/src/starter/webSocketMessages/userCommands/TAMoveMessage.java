package webSocketMessages.userCommands;

public class TAMoveMessage {
    public String authToken;
    public UserGameCommand.CommandType commandType;
    public Integer gameID;
    public TAMove move;

    public static class TAMove {
        public TAPosition startPosition;
        public TAPosition endPosition;
    }

    public static class TAPosition {
        public int column;
        public int row;
    }
}
