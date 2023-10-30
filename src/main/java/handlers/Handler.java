package handlers;

import responses.ResponseParent;
import spark.Request;
import spark.Response;

public abstract class Handler {

    public abstract Object handleRequest(Request req, Response res);

    public void setStatus(Response res, ResponseParent response) {
        if (response.getMessage() == null) {
            res.status(200);
        } else if (response.getMessage().equals("Error: bad request")) {
            res.status(400);
        } else if (response.getMessage().equals("Error: already taken")) {
            res.status(403);
        } else if (response.getMessage().equals("Error: unauthorized")) {
            res.status(401);
        } else {
            res.status(500);
        }
    }
}
