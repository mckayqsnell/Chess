package chess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChessBoardImpl implements ChessBoard {
    //This class stores all the unCaptured pieces in a Game.
    // It needs to support adding and removing pieces for testing, as well as a resetBoard() method that sets the standard Chess starting configuration.

    private final Map<ChessPosition, ChessPiece> boardMap; //So that if there is no piece at a specified position it returns null

    public ChessBoardImpl() {
        boardMap = new HashMap<>();
    }

    @Override
    public Map<ChessPosition, ChessPiece> getBoard() {
        return boardMap;
    }

    @Override
    public void addPiece(ChessPosition position, ChessPiece piece) {
        //Might need to double-check that they don't add too many pieces?
        boardMap.put(position, piece);
    }

    @Override
    public ChessPiece getPiece(ChessPosition position) {
        return boardMap.get(position); //this could return null so that's an easy indicator if a chessPiece is present at that position or not
        //use this when determining capturing or not
    }

    public ChessPosition getPiecePosition(ChessPiece piece) {
        for (Map.Entry<ChessPosition, ChessPiece> entry : boardMap.entrySet()) {
            if (entry.getValue() == piece) {
                return entry.getKey();
            }
        }
        return null; //if the piece is not found on the board
    }

    public void setPieceAtPosition(ChessPosition position, ChessPiece piece) {
        if (piece == null) {
            boardMap.remove(position);
        } else {
            boardMap.put(position, piece);
        }
    }

    public boolean isCaptureMove(ChessMove move) {
        ChessPosition capturePosition = move.getEndPosition();
        return capturePosition != null;
    }

    @Override
    public void resetBoard() //Clear and set chessboard to standard start.
    {
        boardMap.clear(); //empty the board

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
        GsonBuilder builder = new GsonBuilder();
        builder.enableComplexMapKeySerialization();
        builder.registerTypeAdapter(ChessPosition.class, new ChessPositionAdapter());
        builder.registerTypeAdapter(ChessPiece.class, new ChessPieceAdapter());

        Gson gson = builder.create();
        return gson.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoardImpl that = (ChessBoardImpl) o;
        return Objects.equals(boardMap, that.boardMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardMap);
    }
}
