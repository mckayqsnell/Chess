package ui;

import chess.ChessGame;
import responses.*;
import serverFacade.ServerFacade;
import webSocket.ResponseHandler;
import webSocket.WSClient;

import static ui.EscapeSequences.*;

import java.util.*;

public class Client {

    private static String authToken;
    private static Integer gameIdToJoin;
    private static ChessGame.TeamColor currentPlayerColor = null; //Default

    private static Map<Integer, ListGamesResponse.GameInfo> games;

    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        startMenu(serverUrl);
    }

    private static void startMenu(String url) {
        Scanner scanner = new Scanner(System.in);
        welcomeMessage();

        String input = "nothing";
        while (!input.equalsIgnoreCase("quit")) {

            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.print("[LOGGED_OUT]: ");
            input = scanner.nextLine();

            if (input.equalsIgnoreCase("help")) {
                printStartCommands();
            } else if (input.equalsIgnoreCase("register")) {
                boolean notSuccessful = true;
                while (notSuccessful) {
                    if (registerUser(scanner, url)) {
                        loggedInMenu(scanner, url);
                        notSuccessful = false;
                    } else {
                        System.out.println("Registration with server not successful, please try again.");
                    }
                }
            } else if (input.equalsIgnoreCase("login")) {
                boolean notSuccessful = true;
                while (notSuccessful) {
                    if (loginUser(scanner, url)) {
                        loggedInMenu(scanner, url);
                        notSuccessful = false;
                    } else {
                        System.out.println("Login not successful, please try again.");
                    }
                }
            } else if (input.equalsIgnoreCase("quit")) {
                goodbyeMessage();
            } else {
                System.out.println("Not a valid command. Type help for a list of commands");
            }
        }
        scanner.close();
    }

    private static boolean registerUser(Scanner scanner, String url) {
        boolean badInput = true;
        String username = "";
        String password = "";
        String email = "";
        boolean successfulRegistration = false;

        while (badInput) {
            System.out.print("username: ");
            username = scanner.nextLine();
            System.out.print("password: ");
            password = scanner.nextLine();
            System.out.print("email: ");
            email = scanner.nextLine();
            if (!username.isEmpty() && !password.isEmpty() && !email.isEmpty()) {
                badInput = false;
            } else {
                System.out.println("username/password/email is empty, please try again.");
            }
        }

        try {
            RegisterResponse registerResponse = ServerFacade.registerRequest(url, username, password, email);
            if (registerResponse != null) {
                successfulRegistration = true;
                System.out.println("Logged in as " + registerResponse.getUsername());
                authToken = registerResponse.getAuthToken();
            }
        } catch (Exception e) {
            System.out.println("REGISTER USER THREW AN EXCEPTION");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return successfulRegistration;
    }

    private static boolean loginUser(Scanner scanner, String url) {
        boolean badInput = true;
        String username = "";
        String password = "";
        boolean successfulLogin = false;

        while (badInput) {
            System.out.print("username: ");
            username = scanner.nextLine();
            System.out.print("password: ");
            password = scanner.nextLine();
            if (!username.isEmpty() && !password.isEmpty()) {
                badInput = false;
            } else {
                System.out.println("username/password/ is empty, please try again.");
            }
        }

        try {
            LoginResponse loginResponse = ServerFacade.loginRequest(url, username, password);
            if (loginResponse != null) {
                successfulLogin = true;
                System.out.println("Logged in as " + loginResponse.getUsername());
                authToken = loginResponse.getAuthToken();
            }
        } catch (Exception e) {
            System.out.println("LOGIN USER THREW AN EXCEPTION");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return successfulLogin;
    }

    private static void loggedInMenu(Scanner scanner, String url) {
        String input = "";
        while (!input.equalsIgnoreCase("quit")) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
            System.out.print("[LOGGED_IN]: ");
            System.out.print(SET_TEXT_COLOR_WHITE);
            input = scanner.nextLine();

            if (input.equalsIgnoreCase("help")) {
                printLoggedInCommands();
            } else if (input.equalsIgnoreCase("create")) {
                boolean notSuccessful = true;
                while (notSuccessful) {
                    if (createGame(scanner, url, authToken)) {
                        notSuccessful = false;
                    } else {
                        System.out.println("Create game unsuccessful with sever, please try again");
                    }
                }
            } else if (input.equalsIgnoreCase("list")) {
                if (!listGames(url, authToken)) {
                    System.out.println("List games with server unsuccessful");
                }
            } else if (input.equalsIgnoreCase("join")) {
                if (joinGame(scanner, url, false)) {
                    try {
                        var ws = new WSClient(url, new ResponseHandler(currentPlayerColor));
                        GamePlay.playGame(ws, authToken, gameIdToJoin, currentPlayerColor);
                    } catch (Exception e) {
                        System.out.println("EXCEPTION THROWN: WEBSOCKET CONNECTION FAILED IN JOIN IN LOGIN_MENU");
                        System.out.println(e.getMessage());
                    }
                }
            } else if (input.equalsIgnoreCase("observe")) {
                if (joinGame(scanner, url, true)) {
                    try {
                        var ws = new WSClient(url, new ResponseHandler(null));
                        currentPlayerColor = null; //Set to observe
                        GamePlay.playGame(ws, authToken, gameIdToJoin, currentPlayerColor);
                    } catch (Exception e) {
                        System.out.println("EXCEPTION THROWN: WEBSOCKET CONNECTION FAILED IN OBSERVE IN LOGIN_MENU");
                        System.out.println(e.getMessage());
                    }
                }
            } else if (input.equalsIgnoreCase("logout")) {
                if (logout(url, authToken)) {
                    return; //Takes user back to the startMenu
                }
            } else if (input.equalsIgnoreCase("quit")) {
                goodbyeMessage();
                System.exit(0);
            } else {
                System.out.println("Not a valid command. Type help for a list of commands");
            }
        }
    }

    private static boolean createGame(Scanner scanner, String url, String token) {
        if (authToken == null || !authToken.equals(token)) {
            System.out.println("ERROR: Unauthorized create Game. Bad authToken");
            return false;
        }

        boolean badInput = true;
        String gameName = "";
        boolean successful = false;

        while (badInput) {
            System.out.print("game name: ");
            gameName = scanner.nextLine();
            if (!gameName.isEmpty()) {
                badInput = false;
            } else {
                System.out.println("username/password/ is empty, please try again.");
            }
        }

        try {
            CreateGameResponse createGameResponse = ServerFacade.createGameRequest(url, authToken, gameName);
            if (createGameResponse != null) {
                successful = true;
                System.out.println("Game " + "\"" + gameName + "\"" + " created.");
            }
        } catch (Exception e) {
            System.out.println("CREATE GAME THREW AN EXCEPTION");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return successful;
    }

    private static boolean joinGame(Scanner scanner, String url, boolean observe) {
        if (authToken == null) {
            System.out.println("ERROR: Unauthorized joinGame. Bad authToken");
            return false;
        }

        boolean badInput = true;
        int gameNumber = 0;
        String playerColorToSend = "";
        boolean successful = false;

        while (badInput) {
            System.out.print("game number: ");
            gameNumber = scanner.nextInt();
            scanner.nextLine();
            if (!observe) {
                System.out.print("player color: ");
                playerColorToSend = scanner.nextLine();
            }

            if (gameNumber > 0) {
                badInput = false;
            } else if (playerColorToSend.equalsIgnoreCase("white")
                    || playerColorToSend.equalsIgnoreCase("black") || playerColorToSend.isEmpty()) {
                badInput = false;
            } else if (games.containsKey(gameNumber)) { //Check the game map if that number exists
                badInput = false;
            } else {
                System.out.println("invalid game number or player color, please try again.");
            }
        }

        //Not going to lie, was too lazy to fix the playerColor situation, but it works
        if (playerColorToSend.equalsIgnoreCase("white")) {
            currentPlayerColor = ChessGame.TeamColor.WHITE;
            playerColorToSend = "WHITE";
        } else if (playerColorToSend.equalsIgnoreCase("black")) {
            currentPlayerColor = ChessGame.TeamColor.BLACK;
            playerColorToSend = "BLACK";
        } else {
            currentPlayerColor = null;
            playerColorToSend = "";
        }

        //Now that I know the game exists I can grab its ID.
        gameIdToJoin = games.get(gameNumber).getGameID();

        try {
            JoinGameResponse joinGameResponse = ServerFacade.joinGameRequest(url, authToken, gameIdToJoin, playerColorToSend);
            if (joinGameResponse != null) {
                successful = true;
                System.out.println("Joining game: " + gameNumber + " - " + games.get(gameNumber).getGameName());
            }
        } catch (Exception e) {
            System.out.println("JOIN GAME THREW AN EXCEPTION");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return successful;
    }

    private static boolean listGames(String url, String token) {
        if (authToken == null || !authToken.equals(token)) {
            System.out.println("ERROR: Unauthorized listGames. Bad authToken");
            return false;
        }

        boolean successful = false;
        try {
            ListGamesResponse listGamesResponse = ServerFacade.listGamesRequest(url, authToken);
            if (listGamesResponse != null) {
                successful = true;
                games = printListOfGames(listGamesResponse); //Saving this for joinAbility of games
            }
        } catch (Exception e) {
            System.out.println("LIST GAMES THREW AN EXCEPTION");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return successful;
    }

    private static Map<Integer, ListGamesResponse.GameInfo> printListOfGames(ListGamesResponse listGamesResponse) {

        System.out.println("List of all games");

        Set<ListGamesResponse.GameInfo> games = listGamesResponse.getGames();
        Map<Integer, ListGamesResponse.GameInfo> gamesMap = new HashMap<>();

        if (games.isEmpty()) {
            System.out.println("No games currently in session");
        } else {
            int gameNumber = 0;
            for (ListGamesResponse.GameInfo game : games) {
                ++gameNumber;
                gamesMap.put(gameNumber, game);

                System.out.println(gameNumber + ". " + game.getGameName());
                System.out.print("    " + "White: ");
                if (game.getWhiteUsername() != null) {
                    System.out.println(game.getWhiteUsername());
                } else {
                    System.out.println();
                }
                System.out.print("    " + "Black: ");
                if (game.getBlackUsername() != null) {
                    System.out.println(game.getBlackUsername());
                } else {
                    System.out.println();
                }
                System.out.println();
            }
        }

        return gamesMap;
    }

    private static boolean logout(String url, String token) {
        if (authToken == null || !authToken.equals(token)) {
            System.out.println("ERROR: Unauthorized logout. Bad authToken");
            return false;
        }

        boolean successful = false;
        try {
            LogoutResponse logoutResponse = ServerFacade.logoutRequest(url, authToken);
            if (logoutResponse != null) {
                successful = true;
                System.out.println("Logged out");
            }
        } catch (Exception e) {
            System.out.println("LOGOUT THREW AN EXCEPTION");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return successful;
    }

    private static void printLoggedInCommands() {
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print("create");
        System.out.print(SET_TEXT_COLOR_MAGENTA);
        System.out.println(" - a game");

        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print("list");
        System.out.print(SET_TEXT_COLOR_MAGENTA);
        System.out.println(" - games");

        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print("join");
        System.out.print(SET_TEXT_COLOR_MAGENTA);
        System.out.println(" - a game");

        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print("observe");
        System.out.print(SET_TEXT_COLOR_MAGENTA);
        System.out.println(" - a game");

        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print("logout");
        System.out.print(SET_TEXT_COLOR_MAGENTA);
        System.out.println(" - when you are done");

        helpCommandDescriptionHighlights();
    }

    private static void helpCommandDescriptionHighlights() {
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print("quit");
        System.out.print(SET_TEXT_COLOR_MAGENTA);
        System.out.println(" - playing chess");

        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print("help");
        System.out.print(SET_TEXT_COLOR_MAGENTA);
        System.out.println(" - with possible commands");
    }

    private static void welcomeMessage() {
        System.out.println(ERASE_SCREEN);
        System.out.print(SET_TEXT_COLOR_SEAFOAM);
        System.out.print(SET_TEXT_BOLD);
        System.out.print("👑");
        System.out.print(" Welcome to Chess! Type help to get started. ");
        System.out.println("👑");
        System.out.println();
    }

    private static void printStartCommands() {

        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print("register");
        System.out.print(SET_TEXT_COLOR_MAGENTA);
        System.out.println(" - to create an account");

        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print("login");
        System.out.print(SET_TEXT_COLOR_MAGENTA);
        System.out.println(" - to play chess");

        helpCommandDescriptionHighlights();
        System.out.println();
    }

    private static void goodbyeMessage() {
        System.out.println(SET_TEXT_COLOR_SEAFOAM);
        System.out.print("🚀");
        System.out.print(" So long and thanks for all the fish! ");
        System.out.println("🚀");
        System.out.println();
    }
}
