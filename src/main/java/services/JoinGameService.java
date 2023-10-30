package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import models.AuthToken;
import requests.JoinGameRequest;
import responses.JoinGameResponse;

import java.util.Set;

/**
 * JoinGameService class for taking in a JoinGameRequest and returning a response if it was successful or not
 * Verifies that the specified game exists,
 * and, if a color is specified, adds the caller as the requested color to the game.
 * If no color is specified the user is joined as an observer. This request is idempotent.
 */
public class JoinGameService {
    private final AuthDAO authDAO;

    private final GameDAO gameDAO;

    public JoinGameService() {
        authDAO = AuthDAO.getInstance();
        gameDAO = GameDAO.getInstance();
    }

    public JoinGameResponse joinGame(JoinGameRequest request, String authToken) {
        try {
            if (authDAO.findAuthToken(authToken) == null) {
                return unauthorizedResponse();
            }
            if (request.getGameID() == null || gameDAO.findGame(request.getGameID()) == null) {
                return badRequestError();
            }

            if (request.getPlayerColor() == null || request.getPlayerColor().isEmpty()) {
                return new JoinGameResponse(); //Observers, don't need to update anything in the database

            } else if (request.getPlayerColor().equals("WHITE")) {
                String username = findUsernameFromAuthToken(authToken);
                gameDAO.claimSpot(request.getGameID(), username, "WHITE");

                return new JoinGameResponse();

            } else if (request.getPlayerColor().equals("BLACK")) {
                String username = findUsernameFromAuthToken(authToken);
                gameDAO.claimSpot(request.getGameID(), username, "BLACK");

                return new JoinGameResponse();

            } else {

                throw new DataAccessException("Error: already taken");
            }

        } catch (DataAccessException DAE) {
            JoinGameResponse response = new JoinGameResponse();
            response.setMessage(DAE.getMessage());

            return response;
        }
    }

    private JoinGameResponse unauthorizedResponse() {
        JoinGameResponse failureResponse = new JoinGameResponse();
        failureResponse.setMessage("Error: unauthorized");

        return failureResponse;
    }

    private JoinGameResponse badRequestError() {
        JoinGameResponse joinGameResponse = new JoinGameResponse();
        joinGameResponse.setMessage("Error: bad request");

        return joinGameResponse;
    }

    private String findUsernameFromAuthToken(String authToken) throws DataAccessException {
        Set<AuthToken> authTokens = authDAO.findAllAuthTokens();
        if (authTokens.isEmpty()) {
            throw new DataAccessException("No users in the database!");
        }
        String username = "";
        for (AuthToken token : authTokens) {
            if (token.getAuthToken().equals(authToken)) {
                username = token.getUsername();
            }
        }

        if (username == null || username.isEmpty()) {
            throw new DataAccessException("No user in the database matches that authToken!");
        }

        return username;
    }
}
