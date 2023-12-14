package ui;

import chess.*;
import webSocket.WSClient;
import webSocketMessages.userCommands.*;

import static ui.EscapeSequences.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

public class GamePlay {
    // The reference will change to whatever is sent from the server
    private static ChessGameImpl game = new ChessGameImpl();

    private static final Map<Character, Integer> columns = setColumnMap();

    public static void playGame(WSClient ws, String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        //Send a joinPlayerCommand or observeCommand so that everyone knows they joined
        joinPlayer(ws, authToken, gameID, playerColor);

        Scanner scanner = new Scanner(System.in);
        String input = "";
        printWelcomeMessage();
        while (!input.equalsIgnoreCase("LEAVE")) {
            System.out.print(SET_TEXT_COLOR_GREEN);
            System.out.print("[GAMEPLAY]: ");
            System.out.print(SET_TEXT_COLOR_WHITE);

            input = scanner.nextLine();
            if (input.equalsIgnoreCase("help")) {
                printGameCommands();
            } else if (input.equalsIgnoreCase("redraw")) {
                redrawChessBoard(playerColor);
            } else if (input.equalsIgnoreCase("make move")) {
                if (playerColor == null) {
                    System.out.println(SET_TEXT_COLOR_RED);
                    System.out.println("Observers cannot make moves.");
                } else {
                    makeMove(ws, authToken, gameID);
                }
            } else if (input.equalsIgnoreCase("resign")) {
                if (playerColor == null) {
                    System.out.println(SET_TEXT_COLOR_RED);
                    System.out.println("Observers cannot resign.");
                } else {
                    resign(ws, authToken, gameID);
                }
            } else if (input.equalsIgnoreCase("highlight legal moves")) {
                highlightLegalMoves(scanner, playerColor);

                //reset squares
                DrawBoard.setStartPosition(null);
                DrawBoard.setSquaresToHighlight(new HashSet<>());
            } else if (input.equalsIgnoreCase("leave")) {
                leave(ws, authToken, gameID);
                return;
            } else {
                System.out.println("Not a valid command. Type help for command options");
            }
        }
        scanner.close();
    }

    private static void joinPlayer(WSClient ws, String authToken, Integer gameID, ChessGame.TeamColor teamColor) {
        if (teamColor != null) {
            try {
                JoinPlayerCommand joinPlayerCommand = new JoinPlayerCommand(authToken, gameID, teamColor);
                ws.joinPlayer(joinPlayerCommand);
            } catch (Exception e) {
                System.out.println("Exception caught in joinPlayer when trying to regular join");
                System.out.println(e.getMessage());
            }
        } else {
            try {
                JoinObserverCommand joinObserverCommand = new JoinObserverCommand(authToken, gameID);
                ws.observePlayer(joinObserverCommand);
            } catch (Exception e) {
                System.out.println("Exception caught in joinPlayer when trying to observe");
                System.out.println(e.getMessage());
            }
        }
    }

    private static void printWelcomeMessage() {
        System.out.println(RESET_BG_COLOR);
        System.out.print(SET_TEXT_COLOR_BLUE);
        System.out.println("Welcome to game play mode! Type help for a list of commands");
        System.out.println();
    }

    public static void redrawChessBoard(ChessGame.TeamColor playerColor) {
        //For an observer just draw the game in white's perspective
        if (playerColor == null) {
            playerColor = ChessGame.TeamColor.WHITE;
        }
        DrawBoard.drawChessboard(game, playerColor, null);
    }

    private static void makeMove(WSClient ws, String authToken, Integer gameID) {
        ChessMoveImpl move = getMoveFromUser();
        MakeMoveCommand makeMoveCommand = new MakeMoveCommand(authToken, gameID, move);

        try {
            ws.sendMove(makeMoveCommand);
        } catch (Exception e) {
            System.out.println("EXCEPTION thrown when trying to send a move");
            System.out.println(e.getMessage());
        }
    }

    private static ChessMoveImpl getMoveFromUser() {
        Scanner scanner = new Scanner(System.in);

        String startPosString = getStartPositionString(scanner);
        String endPosString = getEndPositionString(scanner);

        int startPosColumn = columns.get(startPosString.charAt(0));
        int startPosRow = Integer.parseInt(String.valueOf(startPosString.charAt(1)));
        int endPosColumn = columns.get(endPosString.charAt(0));
        int endPosRow = Integer.parseInt(String.valueOf(endPosString.charAt(1)));

        ChessPositionImpl startPosition = new ChessPositionImpl(startPosColumn, startPosRow);
        ChessPositionImpl endPosition = new ChessPositionImpl(endPosColumn, endPosRow);

        return new ChessMoveImpl(startPosition, endPosition);
    }

    private static String getEndPositionString(Scanner scanner) {
        boolean badInput;
        badInput = true;
        String endPosString = ""; //They will be stuck in the loop until this is set right
        while (badInput) {
            System.out.println("Please input the position to be moved to: <column><row> EX: h2");
            System.out.print("End Position: ");
            endPosString = scanner.nextLine();

            if (validPosInput(endPosString)) {
                //good input
                badInput = false;
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
        return endPosString;
    }

    private static String getStartPositionString(Scanner scanner) {
        boolean badInput = true;
        String startPosString = ""; //They will be stuck in the loop until this is set right
        while (badInput) {
            System.out.println("Please input the current position of the piece: <column><row> EX: h1");
            System.out.print("Current position: ");
            startPosString = scanner.nextLine();
            //Make sure its length 2 and
            if (validPosInput(startPosString)) {
                //good input
                badInput = false;
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
        return startPosString;
    }

    private static boolean validPosInput(String startPosString) {
        return startPosString.length() == 2 &&
                startPosString.charAt(0) >= 'a' && startPosString.charAt(0) <= 'h' &&
                startPosString.charAt(1) >= '1' && startPosString.charAt(1) <= '8';
    }

    private static void resign(WSClient ws, String authToken, Integer gameID) {
        ResignCommand resignCommand = new ResignCommand(authToken, gameID);
        try {
            ws.sendResign(resignCommand);
        } catch (Exception e) {
            System.out.println("Exception thrown when trying to resign");
            System.out.println(e.getMessage());
        }
    }

    private static void highlightLegalMoves(Scanner scanner, ChessGame.TeamColor teamColor) {
        boolean badInput = true;
        String input = "";
        while (badInput) {
            System.out.println("Please input the position the piece where the piece is located. Ex: h1");
            System.out.print("Position: ");
            input = scanner.nextLine();
            //Make sure its length 2, first char is alpha, and 2nd is a number
            if (validPosInput(input)) {
                //good input
                badInput = false;
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }

        int posColumn = columns.get(input.charAt(0));
        int posRow = Integer.parseInt(String.valueOf(input.charAt(1)));

        ChessPosition chessPosition = new ChessPositionImpl(posColumn, posRow);

        //For an observer just draw the game in white's perspective
        if (teamColor == null) {
            teamColor = ChessGame.TeamColor.WHITE;
        }
        DrawBoard.drawChessboard(game, teamColor, chessPosition);
    }

    private static void leave(WSClient ws, String authToken, Integer gameID) {
        LeaveCommand leaveCommand = new LeaveCommand(authToken, gameID);
        try {
            ws.sendLeave(leaveCommand);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printGameCommands() {
        System.out.println();
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print("Help");
        System.out.print(SET_TEXT_COLOR_MAGENTA);
        System.out.println(" - for a list of commands");

        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print("Redraw");
        System.out.print(SET_TEXT_COLOR_MAGENTA);
        System.out.println(" - to redraw the chess board");

        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print("Leave");
        System.out.print(SET_TEXT_COLOR_MAGENTA);
        System.out.println(" - to leave the game");

        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print("Make Move");
        System.out.print(SET_TEXT_COLOR_MAGENTA);
        System.out.println(" - to make a move in chess");

        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print("Resign");
        System.out.print(SET_TEXT_COLOR_MAGENTA);
        System.out.println(" - to forfeit the game");

        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print("Highlight Legal Moves");
        System.out.print(SET_TEXT_COLOR_MAGENTA);
        System.out.println(" - to show all legal moves for a specific piece");
        System.out.println();
    }

    public static void setGame(ChessGameImpl game) {
        GamePlay.game = game;
    }

    private static Map<Character, Integer> setColumnMap() {
        Map<Character, Integer> columns = new HashMap<>();

        columns.put('a', 1);
        columns.put('b', 2);
        columns.put('c', 3);
        columns.put('d', 4);
        columns.put('e', 5);
        columns.put('f', 6);
        columns.put('g', 7);
        columns.put('h', 8);

        return columns;
    }
}
