package webSocketMessages.serverMessages;

import chess.ChessGameImpl;

public class LoadGameMessage extends ServerMessage {
    ChessGameImpl game;

    public LoadGameMessage(ServerMessageType type) {
        super(type);
    }

    public ChessGameImpl getGame() {
        return game;
    }

    public void setGame(ChessGameImpl game) {
        this.game = game;
    }
}
