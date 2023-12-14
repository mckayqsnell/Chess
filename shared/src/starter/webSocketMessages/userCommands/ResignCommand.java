package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand {
    public ResignCommand(String authToken) {
        super(authToken);
        commandType = CommandType.RESIGN;
    }

    public ResignCommand(String authToken, Integer gameID) {
        super(authToken);
        commandType = CommandType.RESIGN;
        setGameID(gameID);
    }
}
