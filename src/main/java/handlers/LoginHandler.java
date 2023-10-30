package handlers;

import com.google.gson.Gson;
import requests.LoginRequest;
import responses.LoginResponse;
import services.LoginService;
import spark.Request;
import spark.Response;

//Logs in an existing user (returns a new authToken).
public class LoginHandler extends Handler {
    private final LoginService loginService;

    public LoginHandler() {
        loginService = new LoginService();
    }

    public Object handleRequest(Request req, Response res) {
        Gson gson = new Gson();
        LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);

        LoginResponse loginResponse = loginService.login(loginRequest);
        setStatus(res, loginResponse);

        return gson.toJson(loginResponse);
    }
}
