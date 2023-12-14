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

                Gson gson = gsonWithAdapters();
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

    public Gson gsonWithAdapters() {
        GsonBuilder builder = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(ChessBoard.class, new ChessBoardAdapter())
                .setPrettyPrinting();
        return builder.create();
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

    public void updateGame(Integer gameID, ChessMoveImpl chessMove) throws DataAccessException, InvalidMoveException {
        var con = database.getConnection();
        String sql = "select ChessGame from games where gameID = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, gameID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    //Query the game’s state (JSON string) from the database
                    String gameString = rs.getString(1);
                    //Deserialize the JSON string to a ChessGame Java object
                    Gson gson = gsonWithAdapters();
                    ChessGameImpl chessGame = gson.fromJson(gameString, ChessGameImpl.class);
                    //Update the state of the ChessGame object
                    //Could throw an invalid Move Exception //
                    chessGame.makeMove(chessMove); // Will be caught by WebSocketHandler and send an Error

                    //Set the status of the game after the move
                    setChessGameStatus(chessGame);

                    //Re-serialize the Chess game to a JSON string
                    String jsonString = chessGameString(chessGame);
                    System.out.println("Updated gameJsonString");
                    System.out.println(jsonString);
                    //Update the game’s JSON string in the database

                    // Use a transaction to ensure consistency
                    con.setAutoCommit(false);
                    updateChessGameString(con, gameID, jsonString);
                }
            }
        } catch (SQLException e) {
            // Rollback the transaction in case of an exception
            try {
                con.rollback();
            } catch (SQLException rollbackException) {
                System.out.println("RollBackException caught in gameDao.updateGame");
                System.out.println(rollbackException.getMessage());
            }

            throw new DataAccessException("Error updating game: " + gameID + "\n " + e.getMessage());
        } finally {
            // Reset auto-commit and close the connection
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    database.closeConnection(con);
                }
            } catch (SQLException closeException) {
                System.out.println("CloseException caught in gameDao.updateGame");
                System.out.println(closeException.getMessage());
            }
        }
    }

    private static void setChessGameStatus(ChessGameImpl chessGame) {
        //Check if the move resulted in check
        if (chessGame.isInCheck(ChessGame.TeamColor.WHITE)) {
            chessGame.setGameStatus(ChessGame.GameStatus.WHITE_IN_CHECK);
        } else if (chessGame.isInCheck(ChessGame.TeamColor.BLACK)) {
            chessGame.setGameStatus(ChessGame.GameStatus.BLACK_IN_CHECK);
        } else {
            chessGame.setGameStatus(ChessGame.GameStatus.IN_PROGRESS);
        }

        //check if checkmated, or stalemate. This overrides all the other status checks above
        if (chessGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            chessGame.setGameStatus(ChessGame.GameStatus.BLACK_WON);
        } else if (chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            chessGame.setGameStatus(ChessGame.GameStatus.WHITE_WON);
        } else if (chessGame.isInStalemate(ChessGame.TeamColor.WHITE)
                || chessGame.isInStalemate(ChessGame.TeamColor.BLACK)) {
            chessGame.setGameStatus(ChessGame.GameStatus.STALEMATE);
        }
    }

    public void updateGameStatus(Integer gameID, ChessGame.GameStatus gameStatus) throws DataAccessException {
        var con = database.getConnection();
        String sql = "select ChessGame from games where gameID = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, gameID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    //Query the game’s state (JSON string) from the database
                    String gameString = rs.getString(1);
                    //Deserialize the JSON string to a ChessGame Java object
                    Gson gson = gsonWithAdapters();
                    ChessGameImpl chessGame = gson.fromJson(gameString, ChessGameImpl.class);
                    //Update the gameStatus of the ChessGame object
                    /* Set the status of the game */
                    chessGame.setGameStatus(gameStatus);

                    //Re-serialize the Chess game to a JSON string
                    String jsonString = chessGameString(chessGame);
                    // Use a transaction to ensure consistency
                    con.setAutoCommit(false);
                    //Update the game’s JSON string in the database
                    updateChessGameString(con, gameID, jsonString);
                }
            }
        } catch (SQLException e) {
            // Rollback the transaction in case of an exception
            try {
                con.rollback();
            } catch (SQLException rollbackException) {
                System.out.println("RollBackException caught in gameDao.updateGame");
                System.out.println(rollbackException.getMessage());
            }

            throw new DataAccessException("Error updating game: " + gameID + "\n " + e.getMessage());
        } finally {
            // Reset auto-commit and close the connection
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    database.closeConnection(con);
                }
            } catch (SQLException closeException) {
                System.out.println("CloseException caught in gameDao.updateGame");
                System.out.println(closeException.getMessage());
            }
        }
    }

    public void removeUserFromGame(Integer gameID, ChessGame.TeamColor teamColor) throws DataAccessException {
        var con = database.getConnection();
        String sql;

        try {
            con.setAutoCommit(false); //Start transaction

            if (teamColor.equals(ChessGame.TeamColor.WHITE)) {
                sql = "UPDATE games SET whiteUsername = NULL WHERE gameID = ?";
            } else {
                sql = "UPDATE games SET blackUsername = NULL WHERE gameID = ?";
            }

            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setInt(1, gameID);
                stmt.executeUpdate();
            }

            con.commit(); //Commit transaction
        } catch (SQLException e) {
            //Rollback the transaction in case of an exception
            try {
                con.rollback();
            } catch (SQLException rollbackException) {
                System.out.println("RollBackException caught in gameDao.removeUserFromGame");
                System.out.println(rollbackException.getMessage());
            }

            throw new DataAccessException("Error updating game: " + gameID + "\n " + e.getMessage());
        } finally {
            //reset auto-commit and close the connection
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    database.closeConnection(con);
                }
            } catch (SQLException closeException) {
                System.out.println("CloseException caught in gameDao.removeUserFromGame");
                System.out.println(closeException.getMessage());
            }
        }
    }

    private void updateChessGameString(Connection con, Integer gameID, String chessGameString) throws DataAccessException {
        String sql = "UPDATE games SET ChessGame = ? WHERE gameID = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, chessGameString);
            stmt.setInt(2, gameID);
            if (stmt.executeUpdate() == 1) {
                System.out.printf("Updated game: %d with new ChessGameString%n", gameID);
            } else {
                System.out.println("Failed to update ChessGameString in game: " + gameID);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
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

    private String chessGameString(ChessGameImpl chessGame) {
        GsonBuilder builder = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(ChessBoard.class, new ChessBoardAdapter())
                .setPrettyPrinting();
        Gson gson = builder.create();

        return gson.toJson(chessGame);
    }
}
