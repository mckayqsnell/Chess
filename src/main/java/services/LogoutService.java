package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import responses.LogoutResponse;

/**
 * LogoutService which takes in a LogoutRequest and returns weather or not it's successful with a LogoutResponse
 */
public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService() {
        authDAO = AuthDAO.getInstance();
    }

    public LogoutResponse logout(String authToken) {
        try {
            if (authDAO.findAuthToken(authToken) == null) {
                return unauthorizedResponse();
            }

            authDAO.removeAuthToken(authToken);

            return new LogoutResponse();

        } catch (DataAccessException DAE) {
            LogoutResponse failureResponse = new LogoutResponse();
            failureResponse.setMessage(DAE.getMessage());

            return failureResponse;
        }
    }

    private LogoutResponse unauthorizedResponse() {
        LogoutResponse failureResponse = new LogoutResponse();
        failureResponse.setMessage("Error: unauthorized");

        return failureResponse;
    }
}