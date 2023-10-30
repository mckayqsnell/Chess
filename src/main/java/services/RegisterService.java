package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import models.AuthToken;
import models.User;
import requests.RegisterRequest;
import responses.RegisterResponse;

/**
 * RegisterService which takes in a registerRequest and returns weather or not it's successful with a registerResponse
 */
public class RegisterService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService() {
        userDAO = UserDAO.getInstance();
        authDAO = AuthDAO.getInstance();
    }

    public RegisterResponse register(RegisterRequest request) {
        try {
            //check to see if that user already exists
            if (userDAO.findUser(request.getUserName()) != null) {
                return alreadyTakenError();
            }
            //check if it's a bad request
            if (request.getPassword() == null || request.getUserName() == null
                    || request.getPassword().isEmpty() || request.getUserName().isEmpty()) {
                return badRequestError();
            }
            //Create a UserModel and send it to the UserDAO
            User user = new User(request.getUserName(), request.getPassword(), request.getEmail());
            userDAO.createUser(user);

            AuthToken authToken = new AuthToken(request.getUserName());
            authDAO.createAuthToken(authToken);

            RegisterResponse successResponse = new RegisterResponse();
            successResponse.setUsername(authToken.getUsername());
            successResponse.setAuthToken(authToken.getAuthToken());

            return successResponse;

        } catch (DataAccessException DAE) {
            RegisterResponse failureResponse = new RegisterResponse();
            failureResponse.setMessage(DAE.getMessage());

            return failureResponse;
        }
    }

    private RegisterResponse badRequestError() {
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setMessage("Error: bad request");

        return registerResponse;
    }

    private RegisterResponse alreadyTakenError() {
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setMessage("Error: already taken");

        return registerResponse;
    }
}
