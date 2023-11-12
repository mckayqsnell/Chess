package dataAccess;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.Game;

import java.sql.Connection;
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

    private GameDAO() {
        // Private constructor to prevent external instantiation
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
        } finally {
            database.closeConnection(conn);
        }
    }

    public Game findGame(Integer gameID) throws DataAccessException {
        var conn = database.getConnection();
        String sql = "select gameID, whiteUsername, blackUsername, gameName, ChessGame from games where gameID = ?";
        Game game = new Game();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                game.setGameID(rs.getInt(1));
                game.setWhiteUsername(rs.getString(2));
                game.setBlackUsername(rs.getString(3));
                game.setGameName(rs.getString(4));
                String gameString = rs.getString(5);

                GsonBuilder builder = new GsonBuilder() //FIXME: Separate into another method
                        .enableComplexMapKeySerialization()
                        .registerTypeAdapter(ChessBoard.class, new ChessBoardAdapter());
                Gson gson = builder.create();
                game.setGame(gson.fromJson(gameString, ChessGameImpl.class));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            database.closeConnection(conn);
        }

        return game;
    }

    public ArrayList<Game> findAll() throws DataAccessException {
        var conn = database.getConnection();
        String sql = "select gameID, whiteUsername, blackUsername, gameName, ChessGame from games";

        ArrayList<Game> games = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Integer gameID = rs.getInt(1);
                String whiteUsername = rs.getString(2);
                String blackUsername = rs.getString(3);
                String gameName = rs.getString(4);
                String gameString = rs.getString(5);

                GsonBuilder builder = new GsonBuilder() //FIXME: Separate into a method
                        .enableComplexMapKeySerialization()
                        .registerTypeAdapter(ChessBoard.class, new ChessBoardAdapter());
                Gson gson = builder.create();
                ChessGameImpl game = gson.fromJson(gameString, ChessGameImpl.class);

                games.add(new Game(gameID, whiteUsername, blackUsername, gameName, game));
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            database.closeConnection(conn);
        }

        return games;
    }

    public void claimSpot(Integer gameID, String username, String team) throws DataAccessException {
        if (team.equals("WHITE")) {
            claimSpotForWhite(gameID, username);
        } else if (team.equals("BLACK")) {
            claimSpotForBlack(gameID, username);
        } else {
            throw new DataAccessException("Error: bad request");
        }
    }

    private void claimSpotForWhite(Integer gameID, String username) throws DataAccessException {
        var con = database.getConnection();
        //check if the username is not null
        String sql = "select whiteUsername from games where gameID = ?";
        checkIfUsernameIsNull(gameID, con, sql);

        //make the update knowing we checked if its null
        sql = "update games " + "set whiteUsername = ? " + "where gameID = ? ";
        executeUsernameUpdate(gameID, username, con, sql);
    }

    private void claimSpotForBlack(Integer gameID, String username) throws DataAccessException {
        var con = database.getConnection();
        //check if the username is not null
        String sql = "select blackUsername from games where gameID = ?";
        checkIfUsernameIsNull(gameID, con, sql);

        //make the update knowing we checked if its null
        sql = "update games " + "set blackUsername = ? " + "where gameID = ? ";
        executeUsernameUpdate(gameID, username, con, sql);
    }

    private void checkIfUsernameIsNull(Integer gameID, Connection con, String sql) throws DataAccessException {
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, gameID);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String blackUsername = rs.getString(1);
                if (blackUsername != null) {
                    database.closeConnection(con);
                    throw new DataAccessException("Error: already taken");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private void executeUsernameUpdate(Integer gameID, String username, Connection con, String sql) throws DataAccessException {
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, gameID);
            if (stmt.executeUpdate() == 1) {
                System.out.println("Updated game" + gameID);
            } else {
                System.out.println("Failed to update game" + gameID);
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            database.closeConnection(con);
        }
    }

    /* Will be used later */
    public void updateGame(Integer gameID) throws DataAccessException { //FIXME
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
                    ChessGameImpl chessGame = gson.fromJson(gameString, ChessGameImpl.class); //FIXME
                    game.setGame(chessGame);
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            database.closeConnection(con);
        }

        //TODO: Update the game with the specified update (fix parameters) and then put it back into the database
        //Update the state of the ChessGame object
        //Re-serialize the Chess game to a JSON string
        //Update the game’s JSON string in the database
    }

    /* I'm guessing this will be needed later */
    public void removeGame(Integer gameID) throws DataAccessException { //TODO
    }

    public void clearAllGames() throws DataAccessException {
        var conn = database.getConnection();
        String sql = "delete from games";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            database.closeConnection(conn);
        }
    }

    private String chessGameString(ChessGameImpl chessGame) { //TODO: Make this a private method once its tested //DONT NEED TO TEST THIS
        GsonBuilder builder = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(ChessBoard.class, new ChessBoardAdapter());
        Gson gson = builder.create();

        //System.out.println(jsonString);

        //Try making a new ChessGame Object
        //ChessGameImpl testGame = gson.fromJson(jsonString, ChessGameImpl.class);

        //System.out.println(testGame.getBoard().toString());

        return gson.toJson(chessGame);
    }
}
