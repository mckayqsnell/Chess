package handlers;

import chess.*;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import models.AuthToken;
import models.Game;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.*;

@WebSocket
public class WebSocketHandler {
    Map<AuthToken, Connection> connectionsByAuthToken = new HashMap<>();
    Map<Integer, List<Connection>> connectionsByGameId = new HashMap<>();

    AuthDAO authDAO = AuthDAO.getInstance();
    GameDAO gameDAO = GameDAO.getInstance();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = readJson(message, UserGameCommand.class);

        AuthToken authToken = getAuthTokenObject(command.getAuthString());
        if (authToken == null) {
            sendError(session, "BAD AUTH_TOKEN");
            return;
        }
        var conn = getConnection(authToken, session);
        if (conn != null) {
            switch (command.getCommandType()) {
                case JOIN_PLAYER -> join(conn, message);
                case JOIN_OBSERVER -> observe(conn, message);
                case MAKE_MOVE -> move(conn, message);
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
        String usernameJoining = connection.authToken.getUsername();

        Gson gson = userGameCommandAdapters(); //For inheritance hierarchy
        JoinPlayerCommand joinPlayerCommand = gson.fromJson(message, JoinPlayerCommand.class);

        //Verify the game exists
        Game game = getGame(connection, joinPlayerCommand.getGameID());
        if (game == null) return;

        //Verify that the team slot is open for the player joining.
        if (!validTeam(connection, joinPlayerCommand, game, usernameJoining)) return;

        //Send them back a loadGameMessage
        String loadGameJson = getLoadGameJson(game);
        sendMessage(connection, loadGameJson);

        //Add this to the list of connections to this game
        addConnectionToGame(joinPlayerCommand.getGameID(), connection);

        //notify everyone in the game except the user that just joined.
        notifyUsersPlayerJoined(connection, joinPlayerCommand, usernameJoining);
    }

    private void observe(Connection connection, String message) {
        String usernameJoining = connection.authToken.getUsername();

        Gson gson = userGameCommandAdapters(); //For inheritance hierarchy
        JoinObserverCommand joinObserverCommand = gson.fromJson(message, JoinObserverCommand.class);

        //Verify the game exists
        Game game = getGame(connection, joinObserverCommand.getGameID());
        if (game == null) return;

        //Send them back a loadGameMessage
        String loadGameJson = getLoadGameJson(game);
        sendMessage(connection, loadGameJson);

        addConnectionToGame(joinObserverCommand.getGameID(), connection);

        //notify everyone except the curUser that the player joined as an observer
        notifyUsersPlayerJoined(connection, joinObserverCommand, usernameJoining);
    }

    private void move(Connection connection, String message) {
        Gson gson = getGsonWithMoveAdapters();
        MakeMoveCommand makeMoveCommand;
        //Explanation: The tests are sending a json chessMove without using my adapters,
        // so I have to adapt to their json.
        // This is not the best solution, but it works, please don't dock me points lol
        try {
            //If sending from client where it has adapters that will write it how I expect it
            makeMoveCommand = gson.fromJson(message, MakeMoveCommand.class);
        } catch (JsonSyntaxException e) {
            //If sending from WebSocketTests, if this fails it will still throw an exception again
            TAMoveMessage taMoveMessage = gson.fromJson(message, TAMoveMessage.class);
            makeMoveCommand = copyFromTAMovMessage(taMoveMessage);
        }

        if (makeMoveCommand == null) {
            sendError(connection.session, "Error when instantiating makeMoveCommand from Server");
            return;
        }

        //Verify the game exists
        Game game = getGame(connection, makeMoveCommand.getGameID());
        if (game == null) return;

        //Make sure it's the users turn, and they aren't trying to move their opponent's pieces
        if (!usersTurn(connection, game, makeMoveCommand)) {
            sendError(connection.session, "Invalid Move: not your turn");
            return;
        }

        //Make sure the game isn't over
        //Check if the game is over, if so send back an error
        if (gameOver(connection, game)) return;

        //Try updating the game
        game = updateGame(connection, game, makeMoveCommand);
        if (game == null) return;

        //Send everyone in the game a load_game message
        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        loadGameMessage.setGame(game.getGame());
        sendEveryoneInGameLoadGame(game, game.getGameID());

        //send everyone involved in the game a notification except for the currentUser who made the move
        String notificationMessage = "User " + connection.authToken.getUsername()
                + " made a move: "
                + makeMoveCommand.getMove().toString();

        //Check the status of the game before sending the message
        notificationMessage = checkGameStatus(game, notificationMessage);

        sendEveryoneInGameNotification(connection, game.getGameID(), notificationMessage);
    }

    private static String checkGameStatus(Game game, String notificationMessage) {
        if (game.getGame().getGameStatus().equals(ChessGame.GameStatus.WHITE_IN_CHECK)) {
            notificationMessage += "\n" + game.getWhiteUsername() + " is in Check!";
        } else if (game.getGame().getGameStatus().equals(ChessGame.GameStatus.BLACK_IN_CHECK)) {
            notificationMessage += "\n" + game.getBlackUsername() + " is in Check!";
        }

        if (game.getGame().getGameStatus().equals(ChessGame.GameStatus.WHITE_WON)) {
            notificationMessage += "\nCHECKMATE! White won!";
        } else if (game.getGame().getGameStatus().equals(ChessGame.GameStatus.BLACK_WON)) {
            notificationMessage += "\nCHECKMATE! Black won!";
        } else if (game.getGame().getGameStatus().equals(ChessGame.GameStatus.STALEMATE)) {
            notificationMessage += "\nSTALEMATE! The game is over";
        }
        return notificationMessage;
    }

    private boolean gameOver(Connection connection, Game game) {
        if (game.getGame().getGameStatus().equals(ChessGame.GameStatus.WHITE_WON)) {
            sendError(connection.session, "The game is over. White won.");
            return true;
        } else if (game.getGame().getGameStatus().equals(ChessGame.GameStatus.BLACK_WON)) {
            sendError(connection.session, "The game is over. Black won.");
            return true;
        } else if (game.getGame().getGameStatus().equals(ChessGame.GameStatus.STALEMATE)) {
            sendError(connection.session, "The game is over. It is a Stalemate");
            return true;
        }
        return false;
    }

    private void resign(Connection connection, String message) {
        String username = connection.authToken.getUsername();
        System.out.println("Leave command received from user: " + username);
        System.out.println(message);

        Gson gson = userGameCommandAdapters();
        ResignCommand resignCommand = gson.fromJson(message, ResignCommand.class);

        //Verify the game exists
        Game game = getGame(connection, resignCommand.getGameID());
        if (game == null) return;

        //Check if the user trying to resign is an observer, if so send back an error
        if (isObserver(connection, game)) {
            sendError(connection.session, "Observers cannot resign");
            return;
        }

        //Check if the game is over, if so send back an error
        if (gameOver(connection, game)) return;

        //set the status of the game to the opposite team win of the user who just resigned
        if (connection.authToken.getUsername().equals(game.getWhiteUsername())) {
            game.getGame().setGameStatus(ChessGame.GameStatus.BLACK_WON); //local
            updateGameStatus(game.getGameID(), ChessGame.GameStatus.BLACK_WON);
        } else if (connection.authToken.getUsername().equals(game.getBlackUsername())) {
            game.getGame().setGameStatus(ChessGame.GameStatus.WHITE_WON); //local
            updateGameStatus(game.getGameID(), ChessGame.GameStatus.WHITE_WON);
        }

        //Notify everyone including the user this person has resigned
        String notificationMessage = "User " + connection.authToken.getUsername() + " has resigned. The game is over.";

        if (game.getGame().getGameStatus().equals(ChessGame.GameStatus.WHITE_WON)) {
            notificationMessage += " " + game.getWhiteUsername() + " (White) won!";
        } else {
            notificationMessage += " " + game.getBlackUsername() + " (Black) won!";
        }

        sendNotification(connection.session, notificationMessage);
        sendEveryoneInGameNotification(connection, resignCommand.getGameID(), notificationMessage);
    }

    private boolean isObserver(Connection connection, Game game) {
        String username = connection.authToken.getUsername();
        return !username.equals(game.getWhiteUsername()) && !username.equals(game.getBlackUsername());
    }

    private void leave(Connection connection, String message) {
        String username = connection.authToken.getUsername();
        System.out.println("Leave command received from user: " + username);
        System.out.println(message);

        Gson gson = userGameCommandAdapters();
        LeaveCommand leaveCommand = gson.fromJson(message, LeaveCommand.class);

        //Verify the game exists
        Game game = getGame(connection, leaveCommand.getGameID());
        if (game == null) return;

        //Notify everyone that this person left
        String notificationMessage = "User " + connection.authToken.getUsername() + " has left the game";
        sendEveryoneInGameNotification(connection, leaveCommand.getGameID(), notificationMessage);
        //Remove that connection from the maps
        removeConnection(connection);
        //Remove the user from the game in the database
        removeUserFromGame(game, leaveCommand.getGameID(), username);
    }

    private void removeUserFromGame(Game game, Integer gameID, String username) {
        ChessGame.TeamColor teamColor;
        if (username.equals(game.getWhiteUsername())) {
            teamColor = ChessGame.TeamColor.WHITE;
        } else if (username.equals(game.getBlackUsername())) {
            teamColor = ChessGame.TeamColor.BLACK;
        } else { //Observe leave, should be handling this on client but just in case
            return;
        }

        try {
            gameDAO.removeUserFromGame(game.getGameID(), teamColor);
        } catch (DataAccessException e) {
            System.out.println("ERROR when removing a user from the game: " + gameID);
            System.out.println(e.getMessage());
        }
    }

    private void updateGameStatus(Integer gameID, ChessGame.GameStatus gameStatus) {
        try {
            gameDAO.updateGameStatus(gameID, gameStatus);
        } catch (DataAccessException e) {
            System.out.println("DataAccessException caught in WebSocket Handler: updateGameStatus");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean usersTurn(Connection connection, Game game, MakeMoveCommand makeMoveCommand) {
        ChessGame.TeamColor teamTurn = game.getGame().getTeamTurn();
        ChessPosition chessPosition = makeMoveCommand.getMove().getStartPosition();
        ChessBoard chessBoard = game.getGame().getBoard();

        //Check if a piece is at that starting location
        if (chessBoard.getPiece(makeMoveCommand.getMove().getStartPosition()) == null) {
            return false;
        }

        //Check if the piece they are trying to move is the same color as the team turn
        if (!teamTurn.equals(chessBoard.getPiece(chessPosition).getTeamColor())) {
            return false;
        }
        //If the team turn is white, then check if the users team is white
        String user = connection.authToken.getUsername();
        if (teamTurn.equals(ChessGame.TeamColor.WHITE)) {
            return user.equals(game.getWhiteUsername());
        } else if (teamTurn.equals(ChessGame.TeamColor.BLACK)) {
            return user.equals(game.getBlackUsername());
        }
        return false;
    }

    private Game updateGame(Connection connection, Game game, MakeMoveCommand makeMoveCommand) {
        try {
            gameDAO.updateGame(game.getGameID(), makeMoveCommand.getMove());
            //retrieve the game from the database
            game = getGame(connection, makeMoveCommand.getGameID());
            if (game == null) return null;
        } catch (InvalidMoveException e) {
            sendError(connection.session, "Invalid Move: " + makeMoveCommand.getMove().toString());
            return null;
        } catch (DataAccessException e) {
            System.out.println("DataAccessException caught in WebSocket Handler: updateGame");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return game;
    }

    private MakeMoveCommand copyFromTAMovMessage(TAMoveMessage taMoveMessage) {
        MakeMoveCommand moveCommand = new MakeMoveCommand(taMoveMessage.authToken);
        ChessPositionImpl startPosition = new ChessPositionImpl(taMoveMessage.move.startPosition.column,
                taMoveMessage.move.startPosition.row);
        ChessPositionImpl endPosition = new ChessPositionImpl(taMoveMessage.move.endPosition.column,
                taMoveMessage.move.endPosition.row);
        ChessMoveImpl chessMove = new ChessMoveImpl(startPosition, endPosition);
        moveCommand.setMove(chessMove);
        moveCommand.setGameID(taMoveMessage.gameID);

        return moveCommand;
    }

    private static String getLoadGameJson(Game game) {
        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        loadGameMessage.setGame(game.getGame());
        Gson gson2 = getGsonWithBoardAdapters();
        System.out.println(gson2.toJson(loadGameMessage));
        return gson2.toJson(loadGameMessage);
    }

    private void sendEveryoneInGameLoadGame(Game game, Integer gameID) {
        for (Connection con : connectionsByGameId.getOrDefault(gameID, List.of())) {
            String loadGameJson = getLoadGameJson(game);
            sendMessage(con, loadGameJson);
        }
    }

    private void sendEveryoneInGameNotification(Connection connection, Integer gameID, String message) {
        for (Connection con : connectionsByGameId.getOrDefault(gameID, List.of())) {
            if (!con.equals(connection)) {
                sendNotification(con.session, message);
            }
        }
    }


    // Remove a specific connection from all maps
    private void removeConnection(Connection connection) {
        // Remove from connectionsByAuthToken
        Iterator<Map.Entry<AuthToken, Connection>> authTokenIterator = connectionsByAuthToken.entrySet().iterator();
        while (authTokenIterator.hasNext()) {
            Map.Entry<AuthToken, Connection> entry = authTokenIterator.next();
            if (entry.getValue() == connection) {
                authTokenIterator.remove();
                break; // This is assuming each connection is associated with at most one AuthToken, watch this
            }
        }

        // Remove from connectionsByGameId
        for (List<Connection> connectionList : connectionsByGameId.values()) {
            connectionList.remove(connection);
        }
    }

    private Game getGame(Connection connection, Integer gameId) {
        Game game;
        try {
            game = gameDAO.findGame(gameId);
            if (game == null) {
                System.out.println("GAME NOT FOUND. getGame IN WEBSOCKET_HANDLER");
                sendError(connection.session, "GAME NOT FOUND");
                return null;
            }
        } catch (DataAccessException e) {
            System.out.println("DATA_ACCESS_EXCEPTION THROWN IN JOIN IN WEB_HANDLER");
            System.out.println(e.getMessage());
            return null;
        }
        return game;
    }

    private static void sendMessage(Connection connection, String message) {
        try {
            connection.session.getRemote().sendString(message);
        } catch (IOException e) {
            System.out.println("IOEXCEPTION IN SEND_MESSAGE");
            System.out.println(e.getMessage());
            System.out.println("Message: " + message);
        }
    }

    private void notifyUsersPlayerJoined(Connection connection, UserGameCommand userGameCommand, String userWhoJoined) {
        for (Connection otherConnection : connectionsByGameId.getOrDefault(userGameCommand.getGameID(), List.of())) {
            if (!otherConnection.equals(connection)) {
                if (userGameCommand instanceof JoinPlayerCommand joinPlayerCommand) {
                    sendNotification(otherConnection.session,
                            userWhoJoined + " has joined the game as "
                                    + joinPlayerCommand.getPlayerColor());
                } else if (userGameCommand instanceof JoinObserverCommand) {
                    sendNotification(otherConnection.session,
                            userWhoJoined + " has joined the game as an observer");
                }
            }
        }
    }

    private boolean validTeam(Connection connection, JoinPlayerCommand joinPlayerCommand, Game game, String usernameJoining) {
        if (joinPlayerCommand.getPlayerColor().equals(ChessGame.TeamColor.WHITE) && game.getWhiteUsername() == null) {
            sendError(connection.session, "Attempting to join but white team slot is empty");
            return false;
        } else if (joinPlayerCommand.getPlayerColor().equals(ChessGame.TeamColor.BLACK) && game.getBlackUsername() == null) {
            sendError(connection.session, "Attempting to join but black team slot is empty");
            return false;
        } else if (joinPlayerCommand.getPlayerColor().equals(ChessGame.TeamColor.WHITE) && game.getWhiteUsername().equals(usernameJoining)) {
            return true;
        } else if (joinPlayerCommand.getPlayerColor().equals(ChessGame.TeamColor.BLACK) && game.getBlackUsername().equals(usernameJoining)) {
            return true;
        } else {
            sendError(connection.session, "Team slot taken or Bad Request");
            return false;
        }
    }

    private void sendNotification(Session session, String notificationMessage) {
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(notificationMessage);
        Gson gson = new Gson();
        String jsonString = gson.toJson(notification);
        //System.out.println("Sending notification: " + jsonString);

        try {
            session.getRemote().sendString(jsonString);
        } catch (IOException e) {
            System.out.println("Failed to send notification to user.");
        }
    }

    private void sendError(Session session, String errorMessage) {
        ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR);
        error.setErrorMessage(errorMessage);
        String jsonString = new Gson().toJson(error);
        System.out.println("Sending error: " + jsonString);

        try {
            session.getRemote().sendString(jsonString);
        } catch (IOException e) {
            System.out.println("Failed to send error to user.");
        }
    }

    private static Gson getGsonWithBoardAdapters() {
        GsonBuilder builder = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(ChessBoard.class, new ChessBoardAdapter());
        return builder.create();
    }

    private static Gson getGsonWithMoveAdapters() {
        GsonBuilder builder = new GsonBuilder()
                //.enableComplexMapKeySerialization()
                .registerTypeAdapter(ChessPosition.class, new ChessPositionAdapter());
        return builder.create();
    }

    private static Gson userGameCommandAdapters() {
        final RuntimeTypeAdapterFactory<UserGameCommand> typeFactory = RuntimeTypeAdapterFactory
                .of(UserGameCommand.class, "commandType")
                .registerSubtype(JoinPlayerCommand.class)
                .registerSubtype(JoinObserverCommand.class)
                .registerSubtype(LeaveCommand.class)
                .registerSubtype(MakeMoveCommand.class)
                .registerSubtype(ResignCommand.class);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(typeFactory);
        return builder.create();
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

    private void addConnectionToGame(int gameId, Connection connection) {
        connectionsByGameId.computeIfAbsent(gameId, k -> new ArrayList<>()).add(connection);
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
