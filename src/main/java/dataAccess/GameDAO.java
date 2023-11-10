package dataAccess;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import models.Game;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * GameDAO that stores and manipulates the datastore for all games
 * Supports CRUD operations
 */
public class GameDAO {
    private static GameDAO instance;
    private final Database database;
    private final ArrayList<Game> games;

    private GameDAO() {
        // Private constructor to prevent external instantiation
        games = new ArrayList<>();
        database = new Database();
    }

    public static GameDAO getInstance() {
        if (instance == null) {
            instance = new GameDAO();
        }
        return instance;
    }

    public void createGame(Game game) throws DataAccessException {
        var conn = database.getConnection();

        String sql = "insert into games (gameID, whiteUsername, blackUsername, gameName, ChessGame) values (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, game.getGameID());
            stmt.setString(2, game.getWhiteUsername());
            stmt.setString(3, game.getBlackUsername());
            stmt.setString(4, game.getGameName());

            String chessGameString = chessGameString(game.getGame());
            stmt.setString(5, chessGameString);


            stmt.execute();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Game findGame(Integer gameID) throws DataAccessException {
        if (!games.isEmpty()) {
            for (Game game : games) {
                if (gameID.equals(game.getGameID())) {
                    return game;
                }
            }
        } else {
            throw new DataAccessException("Error: bad request");
        }
        return null;
    }

    public ArrayList<Game> findAll() throws DataAccessException {
        return games;
    }

    public void claimSpot(Integer gameID, String username, String team) throws DataAccessException {
        if (team.equals("WHITE")) {
            for (Game game : games) {
                if (gameID.equals(game.getGameID())) {
                    if (game.getWhiteUsername() != null) {
                        throw new DataAccessException("Error: already taken");
                    }
                    game.setWhiteUsername(username);
                    return;
                }
            }
        } else if (team.equals("BLACK")) {
            for (Game game : games) {
                if (gameID.equals(game.getGameID())) {
                    if (game.getBlackUsername() != null) {
                        throw new DataAccessException("Error: already taken");
                    }
                    game.setBlackUsername(username);
                    return;
                }
            }
        } else {
            throw new DataAccessException("Error: bad request");
        }
    }

    /* Will be used later */
    public void updateGame(Integer gameID) throws DataAccessException {
        /*
        Query the game’s state (JSON string) from the database
        Deserialize the JSON string to a ChessGame Java object
        Update the state of the ChessGame object
        Re-serialize the Chess game to a JSON string
        Update the game’s JSON string in the database
        */

        //Query the game’s state (JSON string) from the database
        //Deserialize the JSON string to a ChessGame Java object
        var con = database.getConnection();
        String sql = "select gameID, whiteUsername, blackUsername, gameName, ChessGame from games where gameID = ?";

        Game game = new Game();
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, gameID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    game.setGameID(rs.getInt(1));
                    game.setWhiteUsername(rs.getString(2));
                    game.setBlackUsername(rs.getString(3));
                    game.setGameName(rs.getString(4));

                    String gameString = rs.getString(5);
                    Gson gson = new Gson();
                    ChessGameImpl chessGame = gson.fromJson(gameString, ChessGameImpl.class);
                    game.setGame(chessGame);
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        //TODO: Update the game with the specified update (fix parameters) and then put it back into the database
        //Update the state of the ChessGame object
        //Re-serialize the Chess game to a JSON string
        //Update the game’s JSON string in the database
    }

    /* I'm guessing this will be needed later */
    public void removeGame(Integer gameID) throws DataAccessException {
    }

    public void clearAllGames() throws DataAccessException {
        var conn = database.getConnection();
        String sql = "delete from games";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public String chessGameString(ChessGameImpl chessGame) { //TODO: Make this a private method once its tested //DONT NEED TO TEST THIS
        GsonBuilder builder = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .setPrettyPrinting()
                .registerTypeAdapter(ChessPosition.class, new ChessPositionAdapter())
                .registerTypeAdapter(ChessPiece.class, new ChessPieceAdapter());
        Gson gson = builder.create();

        String jsonString = gson.toJson(chessGame);
        System.out.println(jsonString);

        //Try making a new ChessGame Object
        ChessGameImpl testGame = gson.fromJson(jsonString, ChessGameImpl.class);

        return ""; //FIXME: return the gson string once this is working.
    }
}
