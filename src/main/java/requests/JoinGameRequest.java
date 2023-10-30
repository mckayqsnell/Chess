package requests;

/**
 * Request for joining a game
 */
public class JoinGameRequest {
    private final String playerColor;
    private final Integer gameID;

    public JoinGameRequest(Integer gameID, String playerColor) {
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public Integer getGameID() {
        return gameID;
    }
}
