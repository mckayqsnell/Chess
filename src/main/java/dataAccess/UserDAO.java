package dataAccess;

import models.User;

import java.sql.*;

public class UserDAO {
    private static UserDAO instance;
    private final Database database;

    // Private constructor to prevent external instantiation
    private UserDAO() {
        database = new Database();
    }

    public static UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }

    public void createUser(User user) throws DataAccessException {
        var conn = database.getConnection();

        String sql = "insert into users (username, password, email) values (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.execute();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public User findUser(String username) throws DataAccessException {
        var conn = database.getConnection();
        String sql = "select username, password, email from users where username = ?";

        User user = new User();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user.setUsername(rs.getString(1));
                    user.setPassword(rs.getString(2));
                    user.setEmail(rs.getString(3));
                } else {
                    return null;
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return user;
    }

    public void clearAllUsers() throws DataAccessException {
        var conn = database.getConnection();
        String sql = "delete from users";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
