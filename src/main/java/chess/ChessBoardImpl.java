package chess;

import java.util.HashMap;
import java.util.Map;

public class ChessBoardImpl implements ChessBoard {
    //This class stores all the unCaptured pieces in a Game.
    // It needs to support adding and removing pieces for testing, as well as a resetBoard() method that sets the standard Chess starting configuration.

    private final Map<ChessPosition, ChessPiece> board; //So that if there is no piece at a specified position it returns null

    public ChessBoardImpl() {
        board = new HashMap<>();
    }

    @Override
    public Map<ChessPosition, ChessPiece> getBoard() {
        return board;
    }

    @Override
    public void addPiece(ChessPosition position, ChessPiece piece) {
        //Might need to double-check that they don't add too many pieces?
        board.put(position, piece);
    }

    @Override
    public ChessPiece getPiece(ChessPosition position) {
        return board.get(position); //this could return null so that's an easy indicator if a chessPiece is present at that position or not
        //use this when determining capturing or not
    }

    public ChessPosition getPiecePosition(ChessPiece piece) {
        for (Map.Entry<ChessPosition, ChessPiece> entry : board.entrySet()) {
            if (entry.getValue() == piece) {
                return entry.getKey();
            }
        }
        return null; //if the piece is not found on the board
    }

    public void setPieceAtPosition(ChessPosition position, ChessPiece piece) {
        board.put(position, piece);
    }

    public boolean isCaptureMove(ChessMove move) {
        ChessPosition capturePosition = move.getEndPosition();
        return capturePosition != null;
    }

    @Override
    public void resetBoard() //Clear and set chessboard to standard start.
    {
        board.clear(); //empty the board

        //Now that the board is empty, initialize add all the pieces to their standard positions

        //White

        //White Pawns
        addPiece(new ChessPositionImpl(1, 2), new PawnPiece(ChessGame.TeamColor.WHITE));
        addPiece(new ChessPositionImpl(2, 2), new PawnPiece(ChessGame.TeamColor.WHITE));
        addPiece(new ChessPositionImpl(3, 2), new PawnPiece(ChessGame.TeamColor.WHITE));
        addPiece(new ChessPositionImpl(4, 2), new PawnPiece(ChessGame.TeamColor.WHITE));
        addPiece(new ChessPositionImpl(5, 2), new PawnPiece(ChessGame.TeamColor.WHITE));
        addPiece(new ChessPositionImpl(6, 2), new PawnPiece(ChessGame.TeamColor.WHITE));
        addPiece(new ChessPositionImpl(7, 2), new PawnPiece(ChessGame.TeamColor.WHITE));
        addPiece(new ChessPositionImpl(8, 2), new PawnPiece(ChessGame.TeamColor.WHITE));
        //White Rook, Knight, Bishop, Queen, King, Bishop, Knight, Rook
        addPiece(new ChessPositionImpl(1, 1), new RookPiece(ChessGame.TeamColor.WHITE)); //Rook
        addPiece(new ChessPositionImpl(2, 1), new KnightPiece(ChessGame.TeamColor.WHITE)); //Knight
        addPiece(new ChessPositionImpl(3, 1), new BishopPiece(ChessGame.TeamColor.WHITE)); //Bishop
        addPiece(new ChessPositionImpl(4, 1), new QueenPiece(ChessGame.TeamColor.WHITE)); //Queen
        addPiece(new ChessPositionImpl(5, 1), new KingPiece(ChessGame.TeamColor.WHITE)); //King
        addPiece(new ChessPositionImpl(6, 1), new BishopPiece(ChessGame.TeamColor.WHITE)); //Bishop
        addPiece(new ChessPositionImpl(7, 1), new KnightPiece(ChessGame.TeamColor.WHITE)); //Knight
        addPiece(new ChessPositionImpl(8, 1), new RookPiece(ChessGame.TeamColor.WHITE)); //Rook

        //Black

        //Black Pawns
        addPiece(new ChessPositionImpl(1, 7), new PawnPiece(ChessGame.TeamColor.BLACK));
        addPiece(new ChessPositionImpl(2, 7), new PawnPiece(ChessGame.TeamColor.BLACK));
        addPiece(new ChessPositionImpl(3, 7), new PawnPiece(ChessGame.TeamColor.BLACK));
        addPiece(new ChessPositionImpl(4, 7), new PawnPiece(ChessGame.TeamColor.BLACK));
        addPiece(new ChessPositionImpl(5, 7), new PawnPiece(ChessGame.TeamColor.BLACK));
        addPiece(new ChessPositionImpl(6, 7), new PawnPiece(ChessGame.TeamColor.BLACK));
        addPiece(new ChessPositionImpl(7, 7), new PawnPiece(ChessGame.TeamColor.BLACK));
        addPiece(new ChessPositionImpl(8, 7), new PawnPiece(ChessGame.TeamColor.BLACK));

        //Black Rook, Knight, Bishop, Queen, King, Bishop, Knight, Rook
        addPiece(new ChessPositionImpl(1, 8), new RookPiece(ChessGame.TeamColor.BLACK)); //Rook
        addPiece(new ChessPositionImpl(2, 8), new KnightPiece(ChessGame.TeamColor.BLACK)); //Knight
        addPiece(new ChessPositionImpl(3, 8), new BishopPiece(ChessGame.TeamColor.BLACK)); //Bishop
        addPiece(new ChessPositionImpl(4, 8), new QueenPiece(ChessGame.TeamColor.BLACK)); //Queen
        addPiece(new ChessPositionImpl(5, 8), new KingPiece(ChessGame.TeamColor.BLACK)); //King
        addPiece(new ChessPositionImpl(6, 8), new BishopPiece(ChessGame.TeamColor.BLACK)); //Bishop
        addPiece(new ChessPositionImpl(7, 8), new KnightPiece(ChessGame.TeamColor.BLACK)); //Knight
        addPiece(new ChessPositionImpl(8, 8), new RookPiece(ChessGame.TeamColor.BLACK)); //Rook

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int row = 8; row >= 1; row--) //iterate over each row starting from row 8
        {
            builder.append("|");
            for (int col = 1; col <= 8; col++) //iterate over each column within a row starting from column 1
            {
                ChessPosition position = new ChessPositionImpl(col, row);
                ChessPiece piece = board.get(position);

                if (piece == null) //Check to see if there is a piece at that position or not
                {
                    builder.append(" |");
                } else {
                    builder.append(piece.getPieceChar()).append("|");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
