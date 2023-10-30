package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import models.AuthToken;
import requests.LoginRequest;
import responses.LoginResponse;

/**
 * LoginService takes in a loginRequest and returns weather or not it's successful with a loginResponse
 */
public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService() {
        userDAO = UserDAO.getInstance();
        authDAO = AuthDAO.getInstance();
    }

    public LoginResponse login(LoginRequest request) {
        String username = request.getUserName();
        String password = request.getPassword();
        try {
            if (userDAO.findUser(username) == null) {
                return unauthorizedResponse();
            } else if (!userDAO.findUser(username).getPassword().equals(password)) {
                return unauthorizedResponse();
            }
            //Generate a new AuthToken
            AuthToken authToken = new AuthToken(username);
            authDAO.createAuthToken(authToken);

            return successfulLogin(username, authToken);

        } catch (DataAccessException DAE) {
            LoginResponse failureResponse = new LoginResponse();
            failureResponse.setMessage(DAE.getMessage());

            return failureResponse;
        }
    }

    private LoginResponse unauthorizedResponse() {
        LoginResponse failureResponse = new LoginResponse();
        failureResponse.setMessage("Error: unauthorized");

        return failureResponse;
    }

    private LoginResponse successfulLogin(String username, AuthToken authToken) {
        LoginResponse successfulResponse = new LoginResponse();
        successfulResponse.setUsername(username);
        successfulResponse.setAuthToken(authToken.getAuthToken());

        return successfulResponse;
    }
}
