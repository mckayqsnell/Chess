package handlers;

import com.google.gson.Gson;

import requests.RegisterRequest;
import responses.RegisterResponse;
import services.RegisterService;
import spark.Request;
import spark.Response;

public class RegisterHandler extends Handler {
    private final RegisterService registerService;

    public RegisterHandler() {
        registerService = new RegisterService();
    }

    public Object handleRequest(Request req, Response res) {
        Gson gson = new Gson();
        RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);

        RegisterResponse registerResponse = registerService.register(registerRequest);
        setStatus(res, registerResponse);

        return gson.toJson(registerResponse);
    }
}
