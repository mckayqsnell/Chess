package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand {

    ChessGame.TeamColor playerColor;

    public JoinPlayerCommand(String authToken) {
        super(authToken);
        commandType = CommandType.JOIN_PLAYER;
    }

    public JoinPlayerCommand(String authToken, Integer gameID, ChessGame.TeamColor teamColor) {
        super(authToken);
        commandType = CommandType.JOIN_PLAYER;
        setGameID(gameID);
        this.playerColor = teamColor;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
