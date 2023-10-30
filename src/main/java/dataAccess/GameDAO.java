package dataAccess;

import models.Game;

import java.util.ArrayList;

/**
 * GameDAO that stores and manipulates the datastore for all games
 * Supports CRUD operations
 */
public class GameDAO {
    private static GameDAO instance;
    private final ArrayList<Game> games;

    private GameDAO() {
        // Private constructor to prevent external instantiation
        games = new ArrayList<>();
    }

    public static GameDAO getInstance() {
        if (instance == null) {
            instance = new GameDAO();
        }
        return instance;
    }

    public void createGame(Game game) throws DataAccessException {
        games.add(game);
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
    }

    /* I'm guessing this will be needed later */
    public void removeGame(Integer gameID) throws DataAccessException {
    }

    public void clearAllGames() throws DataAccessException {
        if (!games.isEmpty()) {
            games.clear();
        }
    }
}
