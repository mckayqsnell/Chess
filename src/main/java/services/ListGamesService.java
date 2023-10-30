package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import models.Game;
import responses.ListGamesResponse;

import java.util.ArrayList;

/**
 * ListGamesService which takes in a ListGamesRequest
 * and returns weather or not it's successful with a ListGamesResponse
 */
public class ListGamesService {
    private final AuthDAO authDAO;

    private final GameDAO gameDAO;

    public ListGamesService() {
        authDAO = AuthDAO.getInstance();
        gameDAO = GameDAO.getInstance();
    }

    public ListGamesResponse listGames(String authToken) {
        try {
            //check if its a valid authToken
            if (authDAO.findAuthToken(authToken) == null) {
                return unauthorizedResponse();
            }

            ListGamesResponse successfulResponse = new ListGamesResponse();
            ArrayList<Game> games = gameDAO.findAll();

            for (Game game : games) {
                successfulResponse.addGame(game.getGameID(), game.getGameName(), game.getWhiteUsername(), game.getBlackUsername());
            }

            return successfulResponse;

        } catch (DataAccessException DAE) {
            ListGamesResponse failureResponse = new ListGamesResponse();
            failureResponse.setMessage(DAE.getMessage());

            return failureResponse;
        }
    }

    private ListGamesResponse unauthorizedResponse() {
        ListGamesResponse failureResponse = new ListGamesResponse();
        failureResponse.setMessage("Error: unauthorized");

        return failureResponse;
    }
}
