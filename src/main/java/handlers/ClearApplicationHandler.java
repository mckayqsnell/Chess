package handlers;

import com.google.gson.Gson;
import responses.ClearApplicationResponse;
import services.ClearApplicationService;
import spark.Request;
import spark.Response;

public class ClearApplicationHandler extends Handler {
    private final ClearApplicationService clearApplicationService;

    public ClearApplicationHandler() {
        clearApplicationService = new ClearApplicationService();
    }

    public Object handleRequest(Request req, Response res) {

        ClearApplicationResponse clearApplicationResponse;
        clearApplicationResponse = clearApplicationService.clearApplication();

        Gson gson = new Gson();
        return gson.toJson(clearApplicationResponse);
    }
}
