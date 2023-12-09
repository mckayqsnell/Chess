package webSocket;

import exception.ResponseException;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WSClient extends Endpoint {
    Session session;

    //TODO: Notification handler?
    public WSClient(String url) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            //TODO: Notification handler?

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    System.out.println(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }

    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
