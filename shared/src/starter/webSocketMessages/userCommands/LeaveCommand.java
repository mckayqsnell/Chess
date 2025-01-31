package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand {
    public LeaveCommand(String authToken) {
        super(authToken);
        commandType = CommandType.LEAVE;
    }

    public LeaveCommand(String authToken, Integer gameID) {
        super(authToken);
        commandType = CommandType.LEAVE;
        this.setGameID(gameID);
    }
}
