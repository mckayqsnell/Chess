package serverFacadeTests;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import responses.*;
import serverFacade.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class ServerFacadeTests {

    private final String url = "http://localhost:8080/";

    @BeforeEach
    public void setup() throws IOException {
        ServerFacade.clear(url);
    }

    @Test
    @Order(1)
    @DisplayName("Register a user")
    public void registerUser() throws IOException {
        RegisterResponse registerResponse = ServerFacade.registerRequest(url, "jeff", "jeffStar", "jeffStar@gmail.com");
        System.out.println(new Gson().toJson(registerResponse));

        String expectedUsername = "jeff";
        assertNotNull(registerResponse);
        assertEquals(expectedUsername, registerResponse.getUsername());
        assertNotNull(registerResponse.getAuthToken());
    }

    @Test
    @Order(2)
    @DisplayName("Duplicate Register user")
    public void badRegisterUser() throws IOException {
        RegisterResponse registerResponse = ServerFacade.registerRequest(url, "jeff", "jeffStar", "jeffStar@gmail.com");
        RegisterResponse registerResponse2 = ServerFacade.registerRequest(url, "jeff", "jeffStar", "jeffStar@gmail.com");
        System.out.println(new Gson().toJson(registerResponse));

        assertNull(registerResponse2);
    }

    @Test
    @Order(3)
    @DisplayName("Login a user")
    public void loginUser() throws IOException {
        RegisterResponse registerResponse = ServerFacade.registerRequest(url, "jeff", "jeffStar", "jeffStar@gmail.com");
        LoginResponse loginResponse = ServerFacade.loginRequest(url, "jeff", "jeffStar");

        assertNotNull(registerResponse);
        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getUsername());
        assertNotNull(loginResponse.getAuthToken());
    }

    @Test
    @Order(4)
    @DisplayName("Bad Login a user")
    public void badLoginUser() throws IOException {
        RegisterResponse registerResponse = ServerFacade.registerRequest(url, "jeff", "jeffStar", "jeffStar@gmail.com");
        //Try logging in with the wrong password
        LoginResponse loginResponse = ServerFacade.loginRequest(url, "jeff", "WRONG PASSWORD");

        assertNotNull(registerResponse);
        assertNull(loginResponse);
    }

    @Test
    @Order(5)
    @DisplayName("Clear Application") // The user does not have access to this method, but for ease of tests I made one
    public void clearApplicationRequest() throws IOException {
        ServerFacade.registerRequest(url, "jeff", "jeffStar", "jeffStar@gmail.com");
        ServerFacade.registerRequest(url, "jeff2", "jeff2Star", "jeff2Star@gmail.com");

        ServerFacade.clear(url);

        LoginResponse loginResponse = ServerFacade.loginRequest(url, "jeff", "jeffStar");
        LoginResponse loginResponse2 = ServerFacade.loginRequest(url, "jeff2", "jeff2Star");

        assertNull(loginResponse);
        assertNull(loginResponse2);
    }

    @Test
    @Order(6)
    @DisplayName("Logout user")
    public void logoutUser() throws IOException {
        RegisterResponse registerResponse = ServerFacade.registerRequest(url, "jeff", "jeffStar", "jeffStar@gmail.com");

        LogoutResponse logoutResponse = ServerFacade.logoutRequest(url, registerResponse.getAuthToken());

        assertNotNull(logoutResponse);
    }

    @Test
    @Order(7)
    @DisplayName("Unauthorized Logout")
    public void badLogoutUser() throws IOException {
        ServerFacade.registerRequest(url, "jeff", "jeffStar", "jeffStar@gmail.com");

        //Try with an invalid authToken
        LogoutResponse logoutResponse = ServerFacade.logoutRequest(url, "BAD_AUTH_TOKEN");

        assertNull(logoutResponse);
    }

    @Test
    @Order(8)
    @DisplayName("Create game")
    public void createGame() throws IOException {
        RegisterResponse registerResponse = ServerFacade.registerRequest(url, "jeff", "jeffStar", "jeffStar@gmail.com");
        CreateGameResponse createGameResponse = ServerFacade.createGameRequest(url, registerResponse.getAuthToken(), "TestGame");

        assertNotNull(createGameResponse);
        assertTrue(createGameResponse.getGameID() >= 1000);
    }

    @Test
    @Order(9)
    @DisplayName("Bad Create Game")
    public void badCreateGame() throws IOException {
        //TODO
        //Try creating a game with missing gameName
        RegisterResponse registerResponse = ServerFacade.registerRequest(url, "jeff", "jeffStar", "jeffStar@gmail.com");
        CreateGameResponse createGameResponse = ServerFacade.createGameRequest(url, registerResponse.getAuthToken(), "");

        assertNull(createGameResponse);
        //Try creating a game with an invalid authToken
        CreateGameResponse createGameResponse2 = ServerFacade.createGameRequest(url, "Invalid authToken", "testGame");

        assertNull(createGameResponse2);
    }

    @Test
    @Order(10)
    @DisplayName("List Games")
    public void listGames() throws IOException {
        RegisterResponse registerResponse = ServerFacade.registerRequest(url, "jeff", "jeffStar", "jeffStar@gmail.com");

        CreateGameResponse response = ServerFacade.createGameRequest(url, registerResponse.getAuthToken(), "testGame");
        CreateGameResponse response2 = ServerFacade.createGameRequest(url, registerResponse.getAuthToken(), "testGame2");
        ListGamesResponse listGamesResponse = ServerFacade.listGamesRequest(url, registerResponse.getAuthToken());

        assertNotNull(listGamesResponse);
        assertNotNull(listGamesResponse.getGames());

        ListGamesResponse expected = new ListGamesResponse();
        expected.addGame(response.getGameID(), "testGame", null, null);
        expected.addGame(response2.getGameID(), "testGame2", null, null);

        System.out.println(expected);
        System.out.println(listGamesResponse);

        assertEquals(expected, listGamesResponse);
    }

    @Test
    @Order(11)
    @DisplayName("Unauthorized List Games")
    public void badListGames() throws IOException {
        RegisterResponse registerResponse = ServerFacade.registerRequest(url, "jeff", "jeffStar", "jeffStar@gmail.com");

        ServerFacade.createGameRequest(url, registerResponse.getAuthToken(), "testGame");
        ServerFacade.createGameRequest(url, registerResponse.getAuthToken(), "testGame2");
        ListGamesResponse listGamesResponse = ServerFacade.listGamesRequest(url, "badAuthToken!");

        assertNull(listGamesResponse);
    }

    @Test
    @Order(12)
    @DisplayName("Join Game")
    public void joinGame() throws IOException {
        RegisterResponse registerResponse = ServerFacade.registerRequest(url, "jeff", "jeffStar", "jeffStar@gmail.com");

        CreateGameResponse createGameResponse = ServerFacade.createGameRequest(url, registerResponse.getAuthToken(), "testGame");

        //Try joining the game as white
        JoinGameResponse joinGameResponse = ServerFacade.joinGameRequest(url, registerResponse.getAuthToken(), createGameResponse.getGameID(), "WHITE");
        assertNotNull(joinGameResponse);
        //Try joining the game as black
        JoinGameResponse joinGameResponse2 = ServerFacade.joinGameRequest(url, registerResponse.getAuthToken(), createGameResponse.getGameID(), "BLACK");
        assertNotNull(joinGameResponse2);
        //Set up expected and compare
        ListGamesResponse expected = new ListGamesResponse();
        expected.addGame(createGameResponse.getGameID(), "testGame", registerResponse.getUsername(), registerResponse.getUsername());
        ListGamesResponse actual = ServerFacade.listGamesRequest(url, registerResponse.getAuthToken());
        assertEquals(expected, actual);

        //create another game and try joining as black first
        CreateGameResponse response = ServerFacade.createGameRequest(url, registerResponse.getAuthToken(), "testGame2");
        //try joining as white
        RegisterResponse registerResponse2 = ServerFacade.registerRequest(url, "dawg", "hello", "dawg");
        ServerFacade.joinGameRequest(url, registerResponse2.getAuthToken(), response.getGameID(), "BLACK");
        //try joining as black for user 1(jeff)
        ServerFacade.joinGameRequest(url, registerResponse.getAuthToken(), response.getGameID(), "WHITE");

        //throw in an observer in there,
        RegisterResponse registerResponse3 = ServerFacade.registerRequest(url, "joe", "joe", "joe");
        ServerFacade.joinGameRequest(url, registerResponse3.getAuthToken(), response.getGameID(), "");

        //Set up expected and test
        expected.addGame(response.getGameID(), "testGame2", registerResponse.getUsername(), registerResponse2.getUsername());

        ListGamesResponse actual2 = ServerFacade.listGamesRequest(url, registerResponse.getAuthToken());

        System.out.println(expected);
        System.out.println(actual2);

        assertEquals(expected, actual2);
    }

    @Test
    @Order(13)
    @DisplayName("Bad Join Game")
    public void badJoinGame() throws IOException {
        RegisterResponse registerResponse = ServerFacade.registerRequest(url, "jeff", "jeffStar", "jeffStar@gmail.com");

        CreateGameResponse response = ServerFacade.createGameRequest(url, registerResponse.getAuthToken(), "testGame");

        //Try joining the game as white
        JoinGameResponse joinGameResponse = ServerFacade.joinGameRequest(url, registerResponse.getAuthToken(), response.getGameID(), "WHITE");
        assertNotNull(joinGameResponse);
        //Try joining the game as white again with a different user, make sure it returns null, and it doesn't change
        RegisterResponse registerResponse2 = ServerFacade.registerRequest(url, "joe", "joe", "joe");
        JoinGameResponse joinGameResponse2 = ServerFacade.joinGameRequest(url, registerResponse2.getAuthToken(), response.getGameID(), "WHITE");

        assertNull(joinGameResponse2);

        //Try an unauthorized Join Game
        JoinGameResponse joinGameResponse3 = ServerFacade.joinGameRequest(url, "BAD AUTH TOKEN", response.getGameID(), "WHITE");
        assertNull(joinGameResponse3);
    }
}
