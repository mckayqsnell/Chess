package handlers;

import dataAccess.AuthDAO;
import models.AuthToken;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.userCommands.UserGameCommand;
import com.google.gson.Gson;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebSocket
public class WebSocketHandler {
    Map<AuthToken, Connection> connectionsByAuthToken = new HashMap<>();
    Map<Integer, List<Connection>> connectionsByGameId = new HashMap<>();

    AuthDAO authDAO = AuthDAO.getInstance();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.println("I made it to the Web Socket Handler onMessage");
        UserGameCommand command = readJson(message, UserGameCommand.class);

        AuthToken authToken = getAuthTokenObject(command.getAuthString());
        if (authToken == null) {
            throw new Exception("AuthToken not found, UNAUTHORIZED");
        }
        var conn = getConnection(authToken, session);
        if (conn != null) {
            switch (command.getCommandType()) {
                case JOIN_PLAYER -> join(conn, message);
                case JOIN_OBSERVER -> observe(conn, message);
                case MAKE_MOVE ->
                        move(conn, message); //TODO: Might need to pass in the move here as well(from move command)
                case LEAVE -> leave(conn, message);
                case RESIGN -> resign(conn, message);
            }
        }
    }

    private <T> T readJson(String message, Class<T> gameCommand) {
        Gson gson = new Gson();
        return gson.fromJson(message, gameCommand);
    }

    private void join(Connection connection, String message) {
        //TODO:
        System.out.println("I made it to the JOIN in the WebSocketHandler");
    }

    private void observe(Connection connection, String message) {
        //TODO:
        System.out.println("I made it to the OBSERVE in the WebSocketHandler");
    }

    private void move(Connection connection, String message) {
        //TODO: I might need to pass the move in here as well and have that passed with the move command itself
        System.out.println("I made it to the MOVE in the WebSocketHandler");
    }

    private void leave(Connection connection, String message) {
        //TODO:
        System.out.println("I made it to the LEAVE in the WebSocketHandler");
    }

    private void resign(Connection connection, String message) {
        //TODO:
        System.out.println("I made it to the RESIGN in the WebSocketHandler");
    }

    private AuthToken getAuthTokenObject(String authTokenString) {
        AuthToken authToken = null;
        try {
            authToken = authDAO.findAuthToken(authTokenString);
        } catch (Exception e) {
            System.out.println("EXCEPTION THROWN IN ON_MESSAGE IN WEB_SOCKET_HANDLER");
            e.printStackTrace();
        }
        return authToken;
    }

    private Connection getConnection(AuthToken authToken, Session session) {
        Connection connection = connectionsByAuthToken.get(authToken);
        if (connection == null) {
            //Validate the auth token
            boolean valid = false;
            try {
                if (authDAO.findAuthToken(authToken.getAuthToken()) != null) {
                    valid = true;
                }
            } catch (Exception e) {
                System.out.println("EXCEPTION THROWN IN GET_CONNECTION IN WEB_SOCKET_HANDLER");
                e.printStackTrace();
            }

            if (valid) {
                connection = new Connection(authToken, session);
                connectionsByAuthToken.put(authToken, connection);
            }
        }
        return connection;
    }

    private record Connection(AuthToken authToken, Session session) {
    }
}
