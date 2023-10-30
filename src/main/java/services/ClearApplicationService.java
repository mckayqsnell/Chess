package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import responses.ClearApplicationResponse;

/**
 * ClearApplicationService which takes in a ClearApplicationGamesRequest
 * and returns weather or not it's successful with a ClearApplicationResponse
 */
public class ClearApplicationService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ClearApplicationService() {
        userDAO = UserDAO.getInstance();
        authDAO = AuthDAO.getInstance();
        gameDAO = GameDAO.getInstance();
    }

    public ClearApplicationResponse clearApplication() {
        try {
            userDAO.clearAllUsers();
            authDAO.clearAllAuthTokens();
            gameDAO.clearAllGames();

            return new ClearApplicationResponse();

        } catch (DataAccessException DAE) {
            ClearApplicationResponse failureResponse = new ClearApplicationResponse();
            failureResponse.setMessage(DAE.getMessage());

            return failureResponse;
        }
    }

}
