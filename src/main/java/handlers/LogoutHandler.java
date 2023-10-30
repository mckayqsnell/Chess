package handlers;

import com.google.gson.Gson;
import responses.LogoutResponse;
import services.LogoutService;
import spark.Request;
import spark.Response;

public class LogoutHandler extends Handler {
    private final LogoutService logoutService;

    public LogoutHandler() {
        logoutService = new LogoutService();
    }

    public Object handleRequest(Request req, Response res) {

        String authToken = req.headers("authorization");
        LogoutResponse logoutResponse = logoutService.logout(authToken);
        setStatus(res, logoutResponse);

        Gson gson = new Gson();
        return gson.toJson(logoutResponse);
    }
}
