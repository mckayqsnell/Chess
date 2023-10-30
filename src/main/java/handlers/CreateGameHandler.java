package handlers;

import com.google.gson.Gson;
import requests.CreateGameRequest;
import responses.CreateGameResponse;
import services.CreateGameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler extends Handler {
    private final CreateGameService createGameService;

    public CreateGameHandler() {
        createGameService = new CreateGameService();
    }

    public Object handleRequest(Request req, Response res) {
        Gson gson = new Gson();
        CreateGameRequest createGameRequest = gson.fromJson(req.body(), CreateGameRequest.class);

        String authToken = req.headers("authorization");
        CreateGameResponse createGameResponse = createGameService.createGame(createGameRequest, authToken);
        setStatus(res, createGameResponse);

        return gson.toJson(createGameResponse);
    }
}
