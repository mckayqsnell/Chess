package webSocketMessages.userCommands;

public class MakeMoveCommand extends UserGameCommand {
    public MakeMoveCommand(String authToken) {
        super(authToken);
        commandType = CommandType.MAKE_MOVE;
    }
    //TODO: Might need to add a ChessMove variable here.
}
