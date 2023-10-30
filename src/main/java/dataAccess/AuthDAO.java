package dataAccess;

import models.AuthToken;

import java.util.HashSet;
import java.util.Set;

/**
 * AuthDAO that stores and manipulates the datastore for all authTokens
 * Supports CRUD operations
 */
public class AuthDAO {
    private static AuthDAO instance;
    private final Set<AuthToken> authTokens;

    private AuthDAO() {
        // Private constructor to prevent external instantiation
        authTokens = new HashSet<>();
    }

    public static AuthDAO getInstance() {
        if (instance == null) {
            instance = new AuthDAO();
        }
        return instance;
    }

    public void createAuthToken(AuthToken authToken) throws DataAccessException {
        authTokens.add(authToken);
    }

    public AuthToken findAuthToken(String authToken) throws DataAccessException {
        for (AuthToken token : authTokens) {
            if (token.getAuthToken().equals(authToken)) {
                return token;
            }
        }
        return null;
    }

    public Set<AuthToken> findAllAuthTokens() throws DataAccessException {
        if (authTokens.isEmpty()) {
            throw new DataAccessException("No authTokens in the database!");
        }
        return authTokens;
    }

    public void removeAuthToken(String authToken) throws DataAccessException {
        authTokens.removeIf(authTokenObject -> authTokenObject.getAuthToken().equals(authToken));
    }

    public void clearAllAuthTokens() throws DataAccessException {
        if (!authTokens.isEmpty()) {
            authTokens.clear();
        }
    }
}
