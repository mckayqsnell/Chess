package models;

import chess.ChessGameImpl;

import java.util.Objects;
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

    public Game(Integer gameID, String whiteUsername, String blackUsername, String gameName, ChessGameImpl game) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }

    public Game(Integer gameID, String whiteUsername, String blackUsername, String gameName) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = new ChessGameImpl();
    }

    public Game(Integer gameID, String gameName) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.game = new ChessGameImpl();
        this.whiteUsername = null;
        this.blackUsername = null;
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

    @Override
    public int hashCode() {
        return Objects.hash(gameID, whiteUsername, blackUsername, gameName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Game game1 = (Game) obj;

        return Objects.equals(this.gameID, game1.gameID) && Objects.equals(this.whiteUsername, game1.whiteUsername) &&
                Objects.equals(this.blackUsername, game1.blackUsername) && Objects.equals(this.gameName, game1.gameName);
    }
}
