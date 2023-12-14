package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static ui.EscapeSequences.*;

public class DrawBoard {

    private static final int BOARD_SIZE_IN_SQUARES = 10;
    private static final int ROW_SIZE_IN_SQUARES = 8;
    private static final String EMPTY = "   ";
    private static Set<ChessPosition> squaresToHighlight = new HashSet<>();
    private static ChessPosition startPosition;
    private static ChessGameImpl chessGame;

    public static void drawChessboard(ChessGameImpl game, ChessGame.TeamColor teamColor,
                                      ChessPosition highlightPosition) {
        //If the user entered Highlight Legal Moves
        if (highlightPosition != null) {
            startPosition = highlightPosition;
            setHighLightSquares(game, highlightPosition);
        }
        chessGame = game;

        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.println(RESET_BG_COLOR);

        //I messed up and did the perspectives opposite.
        // Doing a shortcut where I do the opposite of teamColor is to fix it. It works.

        if (teamColor.equals(ChessGame.TeamColor.BLACK)) {
            drawHeaders(out, ChessGame.TeamColor.WHITE);
            out.println(RESET_BG_COLOR);
            drawBoardRows(out, ChessGame.TeamColor.WHITE, game.getBoard().getBoard());
            drawHeaders(out, ChessGame.TeamColor.WHITE);
            out.println(RESET_BG_COLOR);
        } else if (teamColor.equals(ChessGame.TeamColor.WHITE)) {
            drawHeaders(out, ChessGame.TeamColor.BLACK);
            out.println(RESET_BG_COLOR);
            drawBoardRows(out, ChessGame.TeamColor.BLACK, game.getBoard().getBoard());
            drawHeaders(out, ChessGame.TeamColor.BLACK);
            out.println(RESET_BG_COLOR);
        }
    }

    private static void drawHeaders(PrintStream out, ChessGame.TeamColor teamColor) {
        setBlack(out);
        String[] headers = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};

        //just reverse this list for WHITE's perspective
        if (teamColor.equals(ChessGame.TeamColor.WHITE)) {
            headers = reverseStringArray(headers);
        }

        drawEmptyHeaderSquare(out);
        for (String header : headers) {
            drawHeader(out, header);
        }
        drawEmptyHeaderSquare(out);
    }

    private static void drawHeader(PrintStream out, String headerText) {
        out.print(SET_TEXT_COLOR_BLACK + SET_TEXT_BOLD + headerText);
    }

    private static void drawEmptyHeaderSquare(PrintStream out) {
        setGray(out);
        out.print(EMPTY);
    }

    private static void drawBoardRows(PrintStream out, ChessGame.TeamColor teamColor, Map<ChessPosition,
            ChessPiece> board) {

        if (teamColor.equals(ChessGame.TeamColor.WHITE)) { //BLACK --> row 1 at the top
            for (int i = 1; i <= ROW_SIZE_IN_SQUARES; i++) {
                drawRow(out, i, ChessGame.TeamColor.BLACK, board);
                out.println(RESET_BG_COLOR);
            }
        } else { //WHITE --> Row 8 at the top
            for (int i = ROW_SIZE_IN_SQUARES; i >= 1; i--) {
                drawRow(out, i, ChessGame.TeamColor.WHITE, board);
                out.println(RESET_BG_COLOR);
            }
        }
    }

    private static void drawRow(PrintStream out, int rowNumber, ChessGame.TeamColor teamColor, Map<ChessPosition, ChessPiece> board) {
        if (teamColor.equals(ChessGame.TeamColor.WHITE)) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                drawSquare(out, rowNumber, boardCol, board);
            }
        } else {
            for (int boardCol = BOARD_SIZE_IN_SQUARES - 1; boardCol >= 0; --boardCol) {
                drawSquare(out, rowNumber, boardCol, board);
            }
        }
    }

    private static void drawSquare(PrintStream out, int rowNumber, int colNumber, Map<ChessPosition, ChessPiece> board) {
        if (colNumber == 0 || colNumber == 9 && (rowNumber >= 1 && rowNumber <= ROW_SIZE_IN_SQUARES)) {
            drawBorderSquare(out, rowNumber);
        } else {
            ChessPosition position = new ChessPositionImpl(colNumber, rowNumber);
            ChessPiece piece = board.get(position);
            if ((colNumber + rowNumber) % 2 == 0) {
                if (squaresToHighlight.contains(new ChessPositionImpl(colNumber, rowNumber))) {
                    drawDarkGreenSquare(out, piece);
                } else {
                    drawWhiteSquare(out, piece);
                }
            } else {
                if (squaresToHighlight.contains(new ChessPositionImpl(colNumber, rowNumber))) {
                    drawLightGreenSquare(out, piece);
                } else {
                    drawBlackSquare(out, piece);
                }
            }
        }
    }

    private static void drawBorderSquare(PrintStream out, int rowNumber) {
        setGray(out);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(" " + rowNumber + " ");
    }

    private static void drawBlackSquare(PrintStream out, ChessPiece piece) {
        drawSquareText(out, piece, SET_BG_COLOR_BLACK);
    }

    private static void drawWhiteSquare(PrintStream out, ChessPiece piece) {
        drawSquareText(out, piece, SET_BG_COLOR_WHITE);
    }

    private static void drawDarkGreenSquare(PrintStream out, ChessPiece piece) {
        String bgColor = SET_BG_COLOR_DARK_GREEN;
        if (piece != null) {
            if (chessGame.getBoard().getPiecePosition(piece).equals(startPosition)) {
                bgColor = SET_BG_COLOR_YELLOW;
            }
        }
        drawHighlightText(out, piece, bgColor);
    }

    private static void drawLightGreenSquare(PrintStream out, ChessPiece piece) {
        String bgColor = SET_BG_COLOR_GREEN;

        if (piece != null) {
            if (chessGame.getBoard().getPiecePosition(piece).equals(startPosition)) {
                bgColor = SET_BG_COLOR_YELLOW;
            }
        }
        drawHighlightText(out, piece, bgColor);
    }

    private static void drawHighlightText(PrintStream out, ChessPiece piece, String setBgColor) {
        String squareText = EMPTY;
        if (piece != null) {
            squareText = (" " + piece.getPieceChar().toUpperCase() + " ");
            out.print(SET_TEXT_COLOR_BLACK);
        }
        out.print(setBgColor);
        out.print(squareText);
    }

    private static void drawSquareText(PrintStream out, ChessPiece piece, String setBgColor) {
        String squareText = EMPTY;
        if (piece != null) {
            squareText = (" " + piece.getPieceChar().toUpperCase() + " ");
            if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                out.print(SET_TEXT_COLOR_RED);
            } else if (piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                out.print(SET_TEXT_COLOR_BLUE);
            }
        }
        out.print(setBgColor);
        out.print(squareText);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_BLACK);
    }

    private static void setGray(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_LIGHT_GREY);
    }

    private static String[] reverseStringArray(String[] array) {
        int length = array.length;
        String[] reversedArray = new String[length];

        for (int i = 0; i < length; i++) {
            reversedArray[i] = array[length - 1 - i];
        }

        return reversedArray;
    }

    private static void setHighLightSquares(ChessGameImpl game, ChessPosition position) {
        Collection<ChessMove> validMoves = game.validMoves(position);

        //want to highlight all the endPositions and the start pos itself
        squaresToHighlight.add(position);
        for (ChessMove move : validMoves) {
            squaresToHighlight.add(move.getEndPosition());
        }
    }

    public static void setStartPosition(ChessPosition startPosition) {
        DrawBoard.startPosition = startPosition;
    }

    public static void setSquaresToHighlight(Set<ChessPosition> squaresToHighlight) {
        DrawBoard.squaresToHighlight = squaresToHighlight;
    }
}
