package passoffTests.serverTests;

import org.junit.jupiter.api.*;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.*;
import services.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    private RegisterService registerService;
    private ClearApplicationService clearApplicationService;
    private LoginService loginService;
    private LogoutService logoutService;

    private CreateGameService createGameService;
    private ListGamesService listGamesService;
    private JoinGameService joinGameService;

    @BeforeEach
    public void setup() {
        clearApplicationService = new ClearApplicationService();
        clearApplicationService.clearApplication();
    }

    @Test
    @Order(1)
    @DisplayName("Normal Registration")
    public void testNormalRegistration() {
        RegisterRequest registerRequest = new RegisterRequest("mckaysnell", "byu", "snell10@byu.edu");
        registerService = new RegisterService();
        RegisterResponse response = registerService.register(registerRequest);

        assertNotNull(response);
        assertNull(response.getMessage());
        assertEquals("mckaysnell", response.getUsername());
        assertNotNull(response.getAuthToken());
    }

    @Test
    @Order(2)
    @DisplayName("Bad Request Registration")
    public void testBadRegistration() {
        //Empty username field
        RegisterRequest registerRequest = new RegisterRequest("", "password", "snell10@byu.edu");
        registerService = new RegisterService();
        RegisterResponse response = registerService.register(registerRequest);

        assertNotNull(response);
        assertEquals("Error: bad request", response.getMessage());
        assertNull(response.getUsername());
        assertNull(response.getAuthToken());

        //Null username field
        RegisterRequest registerRequest2 = new RegisterRequest();
        registerService = new RegisterService();
        RegisterResponse response2 = registerService.register(registerRequest2);

        assertNotNull(response2);
        assertEquals("Error: bad request", response2.getMessage());
        assertNull(response2.getUsername());
        assertNull(response2.getAuthToken());
    }

    @Test
    @Order(3)
    @DisplayName("Username Already Taken Registration Error")
    public void testUsernameAlreadyTaken() {
        RegisterRequest registerRequest = new RegisterRequest("mckaysnell", "password", "snell10@byu.edu");
        RegisterRequest registerRequest2 = new RegisterRequest("mckaysnell", "llskdjf", "lsdkjf");
        registerService = new RegisterService();
        RegisterResponse response = registerService.register(registerRequest);
        RegisterResponse response2 = registerService.register(registerRequest2);

        assertNotNull(response2);
        assertEquals("Error: already taken", response2.getMessage());
        assertNull(response2.getUsername());
        assertNull(response2.getAuthToken());
    }

    @Test
    @Order(4)
    @DisplayName("Normal Login")
    public void testNormalLogin() {
        //Register a user
        RegisterRequest registerRequest = new RegisterRequest("user", "password", "test@byu,edu");
        registerService = new RegisterService();
        registerService.register(registerRequest);
        //Login that user
        LoginRequest loginRequest = new LoginRequest("user", "password");
        loginService = new LoginService();
        LoginResponse loginResponse = loginService.login(loginRequest);

        assertNotNull(loginResponse);
        assertNull(loginResponse.getMessage());
        assertNotNull(loginResponse.getAuthToken());
        assertNotNull(loginResponse.getUsername());
    }

    @Test
    @Order(5)
    @DisplayName("Wrong username")
    public void testWrongUsername() {
        //Register a user
        RegisterRequest registerRequest = new RegisterRequest("user", "password", "test@byu,edu");
        registerService = new RegisterService();
        registerService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("userBad", "password");
        loginService = new LoginService();
        LoginResponse loginResponse = loginService.login(loginRequest);

        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getMessage());
        assertEquals("Error: unauthorized", loginResponse.getMessage());
        assertNull(loginResponse.getUsername());
        assertNull(loginResponse.getAuthToken());

    }

    @Test
    @Order(6)
    @DisplayName("Wrong password")
    public void testWrongPassword() {
        //Register a user
        RegisterRequest registerRequest = new RegisterRequest("user", "password", "test@byu,edu");
        registerService = new RegisterService();
        registerService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("user", "password123");
        loginService = new LoginService();
        LoginResponse loginResponse = loginService.login(loginRequest);

        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getMessage());
        assertEquals("Error: unauthorized", loginResponse.getMessage());
        assertNull(loginResponse.getUsername());
        assertNull(loginResponse.getAuthToken());

    }

    @Test
    @Order(7)
    @DisplayName("Logout a user")
    public void testLogout() {
        //Register a user
        RegisterRequest registerRequest = new RegisterRequest("user", "password", "test@byu,edu");
        registerService = new RegisterService();
        registerService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("user", "password");
        loginService = new LoginService();
        LoginResponse response = loginService.login(loginRequest);

        logoutService = new LogoutService();
        LogoutResponse logoutResponse = logoutService.logout(response.getAuthToken());

        //only properties it has is a message
        assertNull(logoutResponse.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Unauthorized logout")
    public void testBadLogout() {
        //Register a user
        RegisterRequest registerRequest = new RegisterRequest("user", "password", "test@byu,edu");
        registerService = new RegisterService();
        registerService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("user", "password");
        loginService = new LoginService();
        LoginResponse response = loginService.login(loginRequest);

        logoutService = new LogoutService();
        LogoutResponse logoutResponse = logoutService.logout("bad authtoken");

        //only properties it has is a message
        assertNotNull(logoutResponse.getMessage());
        assertEquals("Error: unauthorized", logoutResponse.getMessage());
    }

    @Test
    @Order(9)
    @DisplayName("Normal Create Game")
    public void testNormalCreateGame() {
        //Register a user
        RegisterRequest registerRequest = new RegisterRequest("user", "password", "test@byu,edu");
        registerService = new RegisterService();
        registerService.register(registerRequest);
        //Login a user
        LoginRequest loginRequest = new LoginRequest("user", "password");
        loginService = new LoginService();
        LoginResponse loginresponse = loginService.login(loginRequest);

        //user creates a game
        CreateGameRequest createGameRequest = new CreateGameRequest("Alone");
        createGameService = new CreateGameService();
        CreateGameResponse createGameResponse = createGameService.createGame(createGameRequest, loginresponse.getAuthToken());

        assertNotNull(createGameResponse);
        assertNull(createGameResponse.getMessage());
        //GameIDs are between 1000 and 10000 and are random
        assertTrue(createGameResponse.getGameID() > 999);
    }

    @Test
    @Order(10)
    @DisplayName("Bad Request Create Game")
    public void badRequestCreateGame() {
        //Register a user
        RegisterRequest registerRequest = new RegisterRequest("user", "password", "test@byu,edu");
        registerService = new RegisterService();
        registerService.register(registerRequest);
        //Login a user
        LoginRequest loginRequest = new LoginRequest("user", "password");
        loginService = new LoginService();
        LoginResponse loginresponse = loginService.login(loginRequest);

        //user creates a game with empty gameName
        CreateGameRequest createGameRequest = new CreateGameRequest("");
        createGameService = new CreateGameService();
        CreateGameResponse createGameResponse = createGameService.createGame(createGameRequest, loginresponse.getAuthToken());

        assertNotNull(createGameResponse);
        assertNotNull(createGameResponse.getMessage());
        assertEquals("Error: bad request", createGameResponse.getMessage());
        assertNull(createGameResponse.getGameID());

        //user creates a game with null gameName
        CreateGameRequest createGameRequest2 = new CreateGameRequest();
        createGameService = new CreateGameService();
        CreateGameResponse createGameResponse2 = createGameService.createGame(createGameRequest2, loginresponse.getAuthToken());

        assertNotNull(createGameResponse2);
        assertNotNull(createGameResponse2.getMessage());
        assertEquals("Error: bad request", createGameResponse2.getMessage());
        assertNull(createGameResponse2.getGameID());
    }

    @Test
    @Order(11)
    @DisplayName("Create Game Unauthorized")
    public void unauthorizedCreateGame() {
        //Register a user
        RegisterRequest registerRequest = new RegisterRequest("user", "password", "test@byu,edu");
        registerService = new RegisterService();
        registerService.register(registerRequest);
        //Login a user
        LoginRequest loginRequest = new LoginRequest("user", "password");
        loginService = new LoginService();
        LoginResponse loginresponse = loginService.login(loginRequest);

        //user attempts to create a game with a bad authToken
        CreateGameRequest createGameRequest = new CreateGameRequest("Gamename");
        createGameService = new CreateGameService();
        CreateGameResponse createGameResponse = createGameService.createGame(createGameRequest, "Bad authToken");

        assertNotNull(createGameResponse);
        assertNotNull(createGameResponse.getMessage());
        assertEquals("Error: unauthorized", createGameResponse.getMessage());
        assertNull(createGameResponse.getGameID());
    }

    @Test
    @Order(12)
    @DisplayName("Regular Join Game")
    public void testGoodJoinGame() {
        //Register a user
        RegisterRequest registerRequest = new RegisterRequest("user", "password", "test@byu,edu");
        registerService = new RegisterService();
        RegisterResponse response = registerService.register(registerRequest);

        //User creates a game
        CreateGameRequest createGameRequest = new CreateGameRequest("Gamename");
        createGameService = new CreateGameService();
        CreateGameResponse createGameResponse = createGameService.createGame(createGameRequest, response.getAuthToken());

        //User joins game as white
        JoinGameRequest joingameRequest = new JoinGameRequest(createGameResponse.getGameID(), "WHITE");
        joinGameService = new JoinGameService();
        JoinGameResponse joinGame = joinGameService.joinGame(joingameRequest, response.getAuthToken());

        assertNull(joinGame.getMessage()); //should be empty response --> only a 200 code
    }

    @Test
    @Order(13)
    @DisplayName("Unauthorized Join Game")
    public void unauthorizedJoinGame() {
        //Register a user
        RegisterRequest registerRequest = new RegisterRequest("user", "password", "test@byu,edu");
        registerService = new RegisterService();
        RegisterResponse response = registerService.register(registerRequest);

        //User creates a game
        CreateGameRequest createGameRequest = new CreateGameRequest("Gamename");
        createGameService = new CreateGameService();
        CreateGameResponse createGameResponse = createGameService.createGame(createGameRequest, response.getAuthToken());

        //User joins game as white
        JoinGameRequest joingameRequest = new JoinGameRequest(createGameResponse.getGameID(), "WHITE");
        joinGameService = new JoinGameService();
        JoinGameResponse joinGame = joinGameService.joinGame(joingameRequest, "bad authToken");

        assertNotNull(joinGame.getMessage());
        assertEquals("Error: unauthorized", joinGame.getMessage());
    }


    @Test
    @Order(14)
    @DisplayName("Good List games")
    public void testGoodListGames() {
        //Register two users
        RegisterRequest registerRequest = new RegisterRequest("user1", "password", "test@byu,edu");
        registerService = new RegisterService();
        registerService.register(registerRequest);

        RegisterRequest registerRequest2 = new RegisterRequest("user2", "password", "test@byu,edu");
        registerService.register(registerRequest2);

        //Login two users
        LoginRequest loginRequest = new LoginRequest("user1", "password");
        loginService = new LoginService();
        LoginResponse user1 = loginService.login(loginRequest);

        LoginRequest loginRequest2 = new LoginRequest("user2", "password");
        loginService = new LoginService();
        LoginResponse user2 = loginService.login(loginRequest2);

        //User1 creates a couple empty games
        //1
        CreateGameRequest createGameRequest = new CreateGameRequest("alone");
        createGameService = new CreateGameService();
        CreateGameResponse game1 = createGameService.createGame(createGameRequest, user1.getAuthToken());
        //2
        CreateGameRequest createGameRequest2 = new CreateGameRequest("another one");
        CreateGameResponse game2 = createGameService.createGame(createGameRequest2, user1.getAuthToken());

        //Have some users join those games
        joinGameService = new JoinGameService();
        //User 1 join game 1 as White
        JoinGameRequest request1 = new JoinGameRequest(game1.getGameID(), "WHITE");
        joinGameService.joinGame(request1, user1.getAuthToken());

        //User 2 join game 1 as Black
        JoinGameRequest request2 = new JoinGameRequest(game1.getGameID(), "BLACK");
        joinGameService.joinGame(request2, user2.getAuthToken());

        //User 1 join game 2 as Black
        JoinGameRequest request3 = new JoinGameRequest(game2.getGameID(), "BLACK");
        joinGameService.joinGame(request3, user1.getAuthToken());

        //User 2 join game 2 as White
        JoinGameRequest request4 = new JoinGameRequest(game2.getGameID(), "WHITE");
        joinGameService.joinGame(request4, user2.getAuthToken());


        //Expected Array
        ListGamesResponse expectedResponse = new ListGamesResponse();
        expectedResponse.addGame(game1.getGameID(), "alone", "user1", "user2");
        expectedResponse.addGame(game2.getGameID(), "another one", "user2", "user1");

        listGamesService = new ListGamesService();
        ListGamesResponse actualResponse = listGamesService.listGames(user1.getAuthToken());

        System.out.println(actualResponse);

        System.out.println(expectedResponse);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @Order(15)
    @DisplayName("Unauthorized List Games")
    public void unauthorizedListGames() {
        //Register a users
        RegisterRequest registerRequest = new RegisterRequest("user1", "password", "test@byu,edu");
        registerService = new RegisterService();
        RegisterResponse response = registerService.register(registerRequest);

        //User1 creates a couple empty games
        //1
        CreateGameRequest createGameRequest = new CreateGameRequest("alone");
        createGameService = new CreateGameService();
        CreateGameResponse game1 = createGameService.createGame(createGameRequest, response.getAuthToken());
        //2
        CreateGameRequest createGameRequest2 = new CreateGameRequest("another one");
        CreateGameResponse game2 = createGameService.createGame(createGameRequest2, response.getAuthToken());

        //Attempting to list games with a bad authToken
        listGamesService = new ListGamesService();
        ListGamesResponse actualResponse = listGamesService.listGames("Bad authToken");

        ListGamesResponse expectedResponse = new ListGamesResponse();
        expectedResponse.setMessage("Error: unauthorized");

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @Order(16)
    @DisplayName("Clear Application")
    public void clearApplicationTest() {
        //Register a users
        RegisterRequest registerRequest = new RegisterRequest("user1", "password", "test@byu,edu");
        registerService = new RegisterService();
        RegisterResponse response = registerService.register(registerRequest);

        //User1 creates a couple empty games
        //1
        CreateGameRequest createGameRequest = new CreateGameRequest("alone");
        createGameService = new CreateGameService();
        CreateGameResponse game1 = createGameService.createGame(createGameRequest, response.getAuthToken());
        //2
        CreateGameRequest createGameRequest2 = new CreateGameRequest("another one");
        CreateGameResponse game2 = createGameService.createGame(createGameRequest2, response.getAuthToken());

        clearApplicationService = new ClearApplicationService();
        ClearApplicationResponse clearApplicationResponse = clearApplicationService.clearApplication();

        assertNull(clearApplicationResponse.getMessage()); //should just be an empty response
    }
}
