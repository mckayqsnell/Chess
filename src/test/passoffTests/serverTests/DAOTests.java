package passoffTests.serverTests;


import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import models.AuthToken;
import models.User;
import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DAOTests {
    private final AuthDAO authDAO = AuthDAO.getInstance();
    private final UserDAO userDAO = UserDAO.getInstance();
    private final GameDAO gameDAO = GameDAO.getInstance();

    @BeforeEach
    public void setup() throws DataAccessException {
        authDAO.clearAllAuthTokens();
        userDAO.clearAllUsers();
        //TODO: gameDAO.clear
    }

    @AfterEach
    public void cleanup() throws DataAccessException {
        authDAO.clearAllAuthTokens();
    }

    /* AuthDAO Tests */

    @Test
    @Order(1)
    @DisplayName("Create an AuthToken")
    public void testCreateAuthToken() throws DataAccessException {
        authDAO.createAuthToken(new AuthToken("mckay", "someAuthToken"));

        AuthToken actual = authDAO.findAuthToken("someAuthToken");
        AuthToken expected = new AuthToken("mckay", "someAuthToken");

        assertNotNull(actual);
        assertNotNull(actual.getUsername());
        assertNotNull(actual.getAuthToken());
        assertEquals(expected, actual);
    }

    @Test
    @Order(2)
    @DisplayName("Bad create AuthToken")
    public void testBadCreateAuthToken() throws DataAccessException {
        authDAO.createAuthToken(new AuthToken("mckay", "duplicateAuthToken"));

        assertThrows(DataAccessException.class, () -> authDAO.createAuthToken(new AuthToken("mckay", "duplicateAuthToken")));
    }

    @Test
    @Order(3)
    @DisplayName("Find an AuthToken")
    public void testFindAuthToken() throws DataAccessException {
        authDAO.createAuthToken(new AuthToken("testUsername", "testToken"));

        AuthToken expected = new AuthToken("testUsername", "testToken");
        AuthToken actual = authDAO.findAuthToken("testToken");

        assertNotNull(actual.getAuthToken());
        assertNotNull(actual.getUsername());
        assertEquals(expected, actual);
    }

    @Test
    @Order(4)
    @DisplayName("Find Auth Token but doesn't exist")
    public void testNoFindAuthToken() throws DataAccessException {
        authDAO.createAuthToken(new AuthToken("testUsername", "testToken"));

        AuthToken result = authDAO.findAuthToken("NOT A VALID TOKEN");

        assertNull(result);
    }

    @Test
    @Order(5)
    @DisplayName("Remove AuthToken")
    public void removeAuthToken() throws DataAccessException {
        authDAO.createAuthToken(new AuthToken("testUsername", "TestToken"));
        authDAO.removeAuthToken("TestToken");
        AuthToken token = authDAO.findAuthToken("TestToken");

        assertNull(token);
    }

    @Test
    @Order(6)
    @DisplayName("Bad remove AuthToken")
    public void badRemoveAuthToken() throws DataAccessException {
        authDAO.createAuthToken(new AuthToken("testUsername", "TestToken"));
        authDAO.removeAuthToken("Doesn'tExist");

        Set<AuthToken> expected = new HashSet<>();
        expected.add(new AuthToken("testUsername", "TestToken"));
        Set<AuthToken> actual = authDAO.findAllAuthTokens();

        assertEquals(expected, actual);

        //Try removing something that doesn't exist multiple times
        authDAO.removeAuthToken("Doesn'tExist");
        authDAO.removeAuthToken("Doesn'tExist");
        actual = authDAO.findAllAuthTokens();

        assertEquals(expected, actual);
    }

    @Test
    @Order(7)
    @DisplayName("Clear AuthTokens")
    public void testClearAuthTokens() throws DataAccessException {
        //Create a couple authTokens in the database
        authDAO.createAuthToken(new AuthToken("testUsername", "testToken"));
        authDAO.createAuthToken(new AuthToken("test this", "testThat"));
        authDAO.createAuthToken(new AuthToken("lebron", "lebron token"));
        //Clear the authTokens from the database
        authDAO.clearAllAuthTokens();
        //See what we get back
        Set<AuthToken> actual = authDAO.findAllAuthTokens();

        assertNull(actual);
    }

    /* UserDAO Tests*/

    @Test
    @Order(8)
    @DisplayName("Add a user")
    public void testAddUser() throws DataAccessException {
        userDAO.createUser(new User("doggy", "dignity", "dog"));
        User expected = new User("doggy", "dignity", "dog");
        User actual = userDAO.findUser("doggy");

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Order(9)
    @DisplayName("Duplicate user")
    public void testBadAddUser() throws DataAccessException {
        userDAO.createUser(new User("doggy", "dignity", "dog"));

        assertThrows(DataAccessException.class, () -> userDAO.createUser(new User("doggy", "dignity", "dog")));

        userDAO.clearAllUsers();
    }

    @Test
    @Order(10)
    @DisplayName("Find a user")
    public void testFindUser() throws DataAccessException {
        userDAO.createUser(new User("doggy", "dignity", "dog"));
        User expected = new User("doggy", "dignity", "dog");
        User actual = userDAO.findUser("doggy");

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Order(11)
    @DisplayName("User doesn't exist")
    public void testBadFindUser() throws DataAccessException {
        userDAO.createUser(new User("doggy", "dignity", "dog"));
        User actual = userDAO.findUser("DOESNT EXIST");

        assertNull(actual);
    }

    @Test
    @Order(12)
    @DisplayName("Clear All Users")
    public void testClearAllUsers() throws DataAccessException {
        userDAO.createUser(new User("doggy1", "dignity", "dog"));
        userDAO.createUser(new User("doggy2", "dignity", "dog"));
        userDAO.createUser(new User("doggy3", "dignity", "dog"));

        userDAO.clearAllUsers();

        User test1 = userDAO.findUser("doggy1");
        User test2 = userDAO.findUser("doggy2");
        User test3 = userDAO.findUser("doggy3");

        assertNull(test1);
        assertNull(test2);
        assertNull(test3);
    }

    //TODO: gameDAO
}
