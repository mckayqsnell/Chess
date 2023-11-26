package responses;

/**
 * the response to a  CreateGameRequest
 * only has a message and status code(in parent)
 */
public class CreateGameResponse extends ResponseParent {
    private Integer gameID;

    public CreateGameResponse() {
    }

    public CreateGameResponse(Integer gameID) {
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }
}
