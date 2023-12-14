package webSocket;

import chess.ChessGame;
import ui.EscapeSequences;
import ui.GamePlay;
import webSocketMessages.serverMessages.LoadGameMessage;

import java.util.Objects;

public class ResponseHandler {
    ChessGame.TeamColor teamColor;

    public ResponseHandler(ChessGame.TeamColor teamColor) {
        this.teamColor = Objects.requireNonNullElse(teamColor, ChessGame.TeamColor.WHITE);
    }

    public void updateBoard(LoadGameMessage loadGameMessage) {
        //The moment I receive the new game I want to redraw the board right after setting it
        GamePlay.setGame(loadGameMessage.getGame());
        GamePlay.redrawChessBoard(teamColor);

        System.out.println();
        System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
        System.out.print("[GAMEPLAY]: ");
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    public void message(String message) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW);
        System.out.println("Notification: " + message);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
        System.out.print("[GAMEPLAY]: ");
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    public void error(String errorMessage) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED);
        System.out.println("Error: " + errorMessage);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
        System.out.print("[GAMEPLAY]: ");
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
    }
}
