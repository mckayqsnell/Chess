package handlers;

import com.google.gson.Gson;
import requests.JoinGameRequest;
import responses.JoinGameResponse;
import services.JoinGameService;
import spark.Request;
import spark.Response;

public class JoinGameHandler extends Handler {
    private final JoinGameService joinGameService;

    public JoinGameHandler() {
        joinGameService = new JoinGameService();
    }

    public Object handleRequest(Request req, Response res) {
        Gson gson = new Gson();
        JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);

        String authToken = req.headers("authorization");
        JoinGameResponse joinGameResponse = joinGameService.joinGame(joinGameRequest, authToken);
        setStatus(res, joinGameResponse);

        return gson.toJson(joinGameResponse);
    }
}
