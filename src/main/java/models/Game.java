package models;

import chess.ChessGame;
import chess.ChessGameImpl;

import java.util.Random;

/**
 * model Game that stores game attributes and the associated ChessGame
 */
public class Game {
    private Integer gameID;

    private String whiteUsername;
    private String blackUsername;
    private final String gameName;
    private final ChessGame game;

    public Game(String gameName) {
        this.gameName = gameName;
        this.game = new ChessGameImpl();
        generateGameID();
    }

    public Integer getGameID() {
        return gameID;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public String getGameName() {
        return gameName;
    }

    public void generateGameID() {
        Random random = new Random();
        int minGameID = 1000;
        int maxGameID = 10000;
        this.gameID = random.nextInt(maxGameID - minGameID) + minGameID;
    }
}
