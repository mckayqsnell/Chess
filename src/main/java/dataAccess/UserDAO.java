package dataAccess;

import models.User;

import java.util.HashSet;
import java.util.Set;

/**
 * UserDAO that stores and manipulates the datastore for all users
 * Supports CRUD operations
 */
public class UserDAO {
    private static UserDAO instance;
    private final Set<User> users;

    private UserDAO() {
        // Private constructor to prevent external instantiation
        users = new HashSet<>();
    }

    public static UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }

    public void createUser(User user) throws DataAccessException {
        for (User u : users) {
            if (u.getUsername().equals(user.getUsername())) {
                throw new DataAccessException("Error: already taken");
            }
        }
        users.add(user);
    }

    public User findUser(String username) throws DataAccessException {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user; //should be safe since I'm using a set
            }
        }
        return null; //FIXME: TEMP RETURN NULL, not sure If should return null if not found or not, might need to throw
    }

    /* Guessing this will be needed later on */
    public void updateUser(String username) throws DataAccessException {
    }

    /* Guessing this will be needed later on */
    public void removeUser(String username) throws DataAccessException {
    }

    public void clearAllUsers() throws DataAccessException {
        if (!users.isEmpty()) {
            users.clear();
        }
    }
}
