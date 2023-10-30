package handlers;

import com.google.gson.Gson;
import responses.ListGamesResponse;
import services.ListGamesService;
import spark.Request;
import spark.Response;

public class ListGamesHandler extends Handler {
    private final ListGamesService listGamesService;

    public ListGamesHandler() {
        listGamesService = new ListGamesService();
    }

    public Object handleRequest(Request req, Response res) {
        String authToken = req.headers("authorization");
        ListGamesResponse listGamesResponse = listGamesService.listGames(authToken);
        setStatus(res, listGamesResponse);

        Gson gson = new Gson();
        return gson.toJson(listGamesResponse);
    }
}
