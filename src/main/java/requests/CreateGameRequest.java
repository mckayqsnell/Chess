package requests;

/**
 * request to create a game
 */
public class CreateGameRequest {
    private String gameName;

    public CreateGameRequest() {
    }

    public CreateGameRequest(String gameName) {
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }

}
