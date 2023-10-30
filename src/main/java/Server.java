import handlers.*;
import spark.Spark;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;

import java.util.Map;

public class Server {
    public static void main(String[] args) {
        new Server().run();
    }

    private void run() {
        Spark.port(8080);

        // Register a directory for hosting static files
        Spark.staticFiles.location("/public");

        createRoutes();

        Spark.awaitInitialization();
        System.out.println("Listening on port 8080!");
    }

    private void createRoutes() {
        //Register a user
        Spark.post("/user", (req, res) -> new RegisterHandler().handleRequest(req, res));

        //ClearApplication
        Spark.delete("/db", (req, res) -> new ClearApplicationHandler().handleRequest(req, res));

        //Login and Logout
        Spark.post("/session", (req, res) -> new LoginHandler().handleRequest(req, res));
        Spark.delete("/session", (req, res) -> new LogoutHandler().handleRequest(req, res));

        //Create a new Chess Game, list all games, join a chess game
        Spark.post("/game", (req, res) -> new CreateGameHandler().handleRequest(req, res));
        Spark.get("/game", (req, res) -> new ListGamesHandler().handleRequest(req, res));
        Spark.put("/game", (req, res) -> new JoinGameHandler().handleRequest(req, res));

        //Errors and uncaught exceptions
        Spark.exception(Exception.class, this::errorHandler);
        Spark.notFound((req, res) -> {
            var msg = String.format("[%s] %s not found", req.requestMethod(), req.pathInfo());
            return errorHandler(new Exception(msg), req, res);
        });
    }

    public Object errorHandler(Exception e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        res.type("application/json");
        res.status(500);
        res.body(body);
        return body;
    }
}
