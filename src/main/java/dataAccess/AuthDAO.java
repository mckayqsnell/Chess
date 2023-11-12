package dataAccess;

import models.AuthToken;

import java.util.HashSet;
import java.util.Set;
import java.sql.*;

public class AuthDAO {
    private static AuthDAO instance;
    private final Database database;

    // Private constructor to prevent external instantiation
    private AuthDAO() {
        database = new Database();
    }

    public static AuthDAO getInstance() {
        if (instance == null) {
            instance = new AuthDAO();
        }
        return instance;
    }

    public void createAuthToken(AuthToken authToken) throws DataAccessException {
        var conn = database.getConnection();

        String sql = "insert into authtokens (authtoken, username) values (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken.getAuthToken());
            stmt.setString(2, authToken.getUsername());
            stmt.execute();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            database.closeConnection(conn);
        }
    }

    public AuthToken findAuthToken(String authToken) throws DataAccessException {
        var conn = database.getConnection();
        String sql = "select authtoken, username from authtokens where authtoken = ?";

        AuthToken token = new AuthToken();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    token.setAuthToken(rs.getString(1));
                    token.setUsername(rs.getString(2));
                } else {
                    return null;
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            database.closeConnection(conn);
        }

        return token;
    }

    public Set<AuthToken> findAllAuthTokens() throws DataAccessException {
        var conn = database.getConnection();
        String sql = "select authtoken, username from authtokens";

        Set<AuthToken> tokens = new HashSet<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String authToken = rs.getString(1);
                String username = rs.getString(2);
                tokens.add(new AuthToken(username, authToken));
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            database.closeConnection(conn);
        }

        if (!tokens.isEmpty()) {
            return tokens;
        }
        return null;
    }

    public void removeAuthToken(String authToken) throws DataAccessException {
        var conn = database.getConnection();
        String sql = "delete from authtokens where authtoken = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            stmt.execute();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            database.closeConnection(conn);
        }
    }

    public void clearAllAuthTokens() throws DataAccessException {
        var conn = database.getConnection();
        String sql = "delete from authtokens";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            database.closeConnection(conn);
        }
    }
}
