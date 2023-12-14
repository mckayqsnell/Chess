package webSocket;

import chess.ChessBoard;
import chess.ChessBoardAdapter;
import chess.ChessPosition;
import chess.ChessPositionAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.ResponseException;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

public class WSClient extends Endpoint {
    Session session;
    ResponseHandler responseHandler;

    public WSClient(String url, ResponseHandler responseHandler) throws ResponseException {
        this.responseHandler = responseHandler;

        try {
            url = url.replace("http", "ws");
            System.out.println(url);
            URI socketURI = new URI(url + "/connect");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    //System.out.println(message);
                    handleIncomingMessage(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void handleIncomingMessage(String message) {
        ServerMessage sm = readJson(message, ServerMessage.class);
        switch (sm.getServerMessageType()) {

            case LOAD_GAME:
                responseHandler.updateBoard(readJson(message, LoadGameMessage.class));
                break;

            case NOTIFICATION:
                responseHandler.message(readJson(message, NotificationMessage.class).getMessage());
                break;

            case ERROR:
                responseHandler.error(readJson(message, ErrorMessage.class).getErrorMessage());
        }
    }

    public void joinPlayer(JoinPlayerCommand joinPlayerCommand) throws Exception {
        String jsonString = new Gson().toJson(joinPlayerCommand);
        send(jsonString);
    }

    public void observePlayer(JoinObserverCommand joinObserverCommand) throws Exception {
        String jsonString = new Gson().toJson(joinObserverCommand);
        send(jsonString);
    }

    public void sendMove(MakeMoveCommand makeMoveCommand) throws Exception {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessPosition.class, new ChessPositionAdapter());
        builder.enableComplexMapKeySerialization();
        String jsonString = builder.create().toJson(makeMoveCommand);
        send(jsonString);
    }

    public void sendLeave(LeaveCommand leaveCommand) throws Exception {
        String jsonString = new Gson().toJson(leaveCommand);
        send(jsonString);
    }

    public void sendResign(ResignCommand resignCommand) throws Exception {
        String jsonString = new Gson().toJson(resignCommand);
        send(jsonString);
    }

    private <T> T readJson(String message, Class<T> clazz) {
        Gson gson = new Gson();
        if (LoadGameMessage.class.isAssignableFrom(clazz)) {
            GsonBuilder builder = new GsonBuilder()
                    .enableComplexMapKeySerialization()
                    .registerTypeAdapter(ChessBoard.class, new ChessBoardAdapter());
            gson = builder.create();
        }
        return gson.fromJson(message, clazz);
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
        // Signal that the server response has been received
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
