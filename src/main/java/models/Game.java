package models;

import chess.ChessGame;
import chess.ChessGameImpl;
import chess.ChessPosition;

import java.util.Random;

/**
 * model Game that stores game attributes and the associated ChessGame
 */
public class Game {
    private Integer gameID;

    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private ChessGameImpl game;

    public Game() {
    }

    public Game(String gameName) {
        this.gameName = gameName;
        this.game = new ChessGameImpl();
        generateGameID();
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
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

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public ChessGameImpl getGame() {
        return game;
    }

    public void generateGameID() {
        Random random = new Random();
        int minGameID = 1000;
        int maxGameID = 10000;
        this.gameID = random.nextInt(maxGameID - minGameID) + minGameID;
    }

    public void setGame(ChessGameImpl game) {
        this.game = game;
    }
}
