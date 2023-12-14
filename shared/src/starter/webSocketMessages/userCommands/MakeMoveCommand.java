package webSocketMessages.userCommands;

import chess.ChessMoveImpl;

public class MakeMoveCommand extends UserGameCommand {
    ChessMoveImpl move;

    public MakeMoveCommand(String authToken) {
        super(authToken);
        commandType = CommandType.MAKE_MOVE;
    }

    public MakeMoveCommand(String authToken, Integer gameID, ChessMoveImpl move) {
        super(authToken);
        commandType = CommandType.MAKE_MOVE;
        setGameID(gameID);
        this.move = move;
    }

    public ChessMoveImpl getMove() {
        return move;
    }

    public void setMove(ChessMoveImpl move) {
        this.move = move;
    }
}
