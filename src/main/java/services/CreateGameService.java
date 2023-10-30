package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import models.Game;
import requests.CreateGameRequest;
import responses.CreateGameResponse;

/**
 * CreateGameService which takes in a CreateGameRequest
 * and returns weather or not it's successful with a CreateGameResponse
 */
public class CreateGameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public CreateGameService() {
        gameDAO = GameDAO.getInstance();
        authDAO = AuthDAO.getInstance();
    }

    public CreateGameResponse createGame(CreateGameRequest request, String authToken) {
        String gameName = request.getGameName();
        try {
            if (authDAO.findAuthToken(authToken) == null) {
                return unauthorizedResponse();
            }
            if (request.getGameName() == null || request.getGameName().isEmpty()) {
                return badRequestError();
            }

            Game game = new Game(gameName);
            gameDAO.createGame(game);

            return new CreateGameResponse(game.getGameID());

        } catch (DataAccessException DAE) {
            CreateGameResponse failureResponse = new CreateGameResponse();
            failureResponse.setMessage(DAE.getMessage());

            return failureResponse;
        }
    }

    private CreateGameResponse unauthorizedResponse() {
        CreateGameResponse failureResponse = new CreateGameResponse();
        failureResponse.setMessage("Error: unauthorized");

        return failureResponse;
    }

    private CreateGameResponse badRequestError() {
        CreateGameResponse createGameResponse = new CreateGameResponse();
        createGameResponse.setMessage("Error: bad request");

        return createGameResponse;
    }
}
