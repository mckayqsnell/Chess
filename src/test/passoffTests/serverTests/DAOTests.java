package passoffTests.serverTests;


import chess.*;
import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import models.AuthToken;
import models.Game;
import models.User;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
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
        gameDAO.clearAllGames();
    }

    @AfterEach
    public void cleanup() throws DataAccessException {
        authDAO.clearAllAuthTokens();
        userDAO.clearAllUsers();
        gameDAO.clearAllGames();
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

    @Test
    @Order(13)
    @DisplayName("Create Game")
    public void testCreateChessGame() throws DataAccessException {
        Game expected = new Game(1001, "whiteUsername", "blackUsername", "testGame");
        gameDAO.createGame(expected);
        Game actual = gameDAO.findGame(1001);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Order(14)
    @DisplayName("Bad Create Game, Duplicate Game")
    public void testBadCreateGame() throws DataAccessException {
        gameDAO.createGame(new Game(1001, "whiteUsername", "blackUsername", "testGame"));

        assertThrows(DataAccessException.class, () -> gameDAO.createGame(new Game(1001, "whiteUsername", "blackUsername", "testGame")));
    }

    @Test
    @Order(15)
    @DisplayName("Find a game")
    public void testFindGame() throws DataAccessException {
        Game expected = new Game(1001, "whiteUsername", "blackUsername", "testGame");
        gameDAO.createGame(expected);
        Game actual = gameDAO.findGame(1001);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Order(16)
    @DisplayName("Game does not exist")
    public void testBadFindGame() throws DataAccessException {
        gameDAO.createGame(new Game(1001, "whiteUsername", "blackUsername", "testGame"));
        gameDAO.createGame(new Game(1002, "whiteUsername", "blackUsername", "testGame2"));
        gameDAO.createGame(new Game(1003, "whiteUsername", "blackUsername", "testGame3"));

        //Try to find a game that does not exist
        Game actual = gameDAO.findGame(2000);
        assertNull(actual);
    }

    @Test
    @Order(17)
    @DisplayName("ClaimSpot")
    public void testClaimSpot() throws DataAccessException {
        gameDAO.createGame(new Game(1001, "testGame"));
        gameDAO.claimSpot(1001, "testUser", "WHITE");

        Game expected = new Game(1001, "testUser", null, "testGame");
        Game actual = gameDAO.findGame(1001);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Order(18)
    @DisplayName("Bad ClaimSpot")
    public void testBadClaimSpot() throws DataAccessException {
        gameDAO.createGame(new Game(1001, "testGame"));
        gameDAO.claimSpot(1001, "testUser", "WHITE");

        //Try to claim that same spot in white that was already claimed
        assertThrows(DataAccessException.class, () -> gameDAO.claimSpot(1001, "someOther", "WHITE"));
    }

    @Test
    @Order(19)
    @DisplayName("List all Games")
    public void listAllGames() throws DataAccessException {
        gameDAO.createGame(new Game(1001, "testGame"));
        gameDAO.createGame(new Game(1002, "testGame2"));
        gameDAO.createGame(new Game(1003, "testGame3"));

        ArrayList<Game> expected = new ArrayList<>();
        expected.add(new Game(1001, "testGame"));
        expected.add(new Game(1002, "testGame2"));
        expected.add(new Game(1003, "testGame3"));

        ArrayList<Game> actual = gameDAO.findAll();

        assertEquals(expected, actual);
    }

    @Test
    @Order(20)
    @DisplayName("Bad List Games")
    public void badListAllGames() throws DataAccessException {
        //Make sure no games are returned if the database should be empty? Couldn't think of a negative case to test
        ArrayList<Game> actual = gameDAO.findAll();
        ArrayList<Game> expected = new ArrayList<>();
        assertEquals(expected, actual); //Should return an empty arrayList
    }

    @Test
    @Order(21)
    @DisplayName("Normal update game")
    public void updateGameTest() throws DataAccessException, InvalidMoveException {
        gameDAO.createGame(new Game(1001, "testGame"));

        //Try updating the game with a mov that is from a white piece position
        ChessMoveImpl chessMove = new ChessMoveImpl(new ChessPositionImpl(1, 2), new ChessPositionImpl(1, 3));
        gameDAO.updateGame(1001, chessMove);

        Game actualGame = gameDAO.findGame(1001);

        assertNotNull(actualGame);
        assertNotNull(actualGame.getGame());

        ChessGameImpl expected = new ChessGameImpl();
        expected.makeMove(chessMove);
        System.out.println("Expected");
        System.out.println(new Gson().toJson(expected));

        ChessGameImpl actual = actualGame.getGame();
        System.out.println("Actual");
        System.out.println(new Gson().toJson(actual));
        assertEquals(expected, actual);
    }

    @Test
    @Order(22)
    @DisplayName("Invalid Move")
    public void invalidMoveTest() throws DataAccessException {
        gameDAO.createGame(new Game(1001, "testGame"));

        //Try updating the game with an invalid move
        ChessMoveImpl chessMove = new ChessMoveImpl(new ChessPositionImpl(1, 7), new ChessPositionImpl(1, 6));
        assertThrows(InvalidMoveException.class, () -> gameDAO.updateGame(1001, chessMove));

        //Make sure the game is unchanged
        Game actualGame = gameDAO.findGame(1001);
        assertNotNull(actualGame);
        assertNotNull(actualGame.getGame());
        ChessGameImpl expected = new ChessGameImpl();
        ChessGameImpl actual = actualGame.getGame();
        assertEquals(expected, actual);
    }

    @Test
    @Order(23)
    @DisplayName("Normal Update Game Status")
    public void updateGameStatusTest() throws DataAccessException {
        gameDAO.createGame(new Game(1001, "testGame"));

        //gameDAO.updateGameStatus(1001, true);
        ChessGameImpl expected = new ChessGameImpl();
        //expected.setGameOver(true);

        ChessGameImpl actual = gameDAO.findGame(1001).getGame();

        assertEquals(expected, actual);
    }

    @Test
    @Order(24)
    @DisplayName("Clear all Games")
    public void clearAllGames() throws DataAccessException {
        gameDAO.createGame(new Game(1001, "testGame"));
        gameDAO.createGame(new Game(1002, "testGame2"));
        gameDAO.createGame(new Game(1003, "testGame3"));
        gameDAO.clearAllGames();
        ArrayList<Game> actual = gameDAO.findAll();
        ArrayList<Game> expected = new ArrayList<>();

        assertEquals(expected, actual);
    }
}
