package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class KingPiece extends ChessPieceImpl {
    public KingPiece(ChessGame.TeamColor teamColor) {
        super(teamColor, PieceType.KING);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //Don't need to take in account if I'm moving into check or not.
        //Chess game will take care of that
        //8 possible moves when you don't take into account out of bounds or if something is in my way
        //NOTE: Doing this in white's perspective, but it will work for black just in reverse.
        int col = myPosition.getColumn();
        int row = myPosition.getRow();
        Set<ChessMove> pieceMoves = new HashSet<>();
        //1. one space to the left --> col-1, row
        ChessPosition left_pos = new ChessPositionImpl(col - 1, row);
        pieceMoves.add(new ChessMoveImpl(myPosition, left_pos));
        //2. one space to the left and up --> col-1, row+1
        ChessPosition diagonal_left_pos = new ChessPositionImpl(col - 1, row + 1);
        pieceMoves.add(new ChessMoveImpl(myPosition, diagonal_left_pos));
        //3. one space up --> col, row+1
        ChessPosition up_pos = new ChessPositionImpl(col, row + 1);
        pieceMoves.add(new ChessMoveImpl(myPosition, up_pos));
        //4. one space to the right and up --> col+1, row+1
        ChessPosition diagonal_right_pos = new ChessPositionImpl(col + 1, row + 1);
        pieceMoves.add(new ChessMoveImpl(myPosition, diagonal_right_pos));
        //5. one space to the right --> col+1, row
        ChessPosition right_pos = new ChessPositionImpl(col + 1, row);
        pieceMoves.add(new ChessMoveImpl(myPosition, right_pos));
        //6. one space to the right and down --> col+1, row-1
        diagonal_right_pos = new ChessPositionImpl(col + 1, row - 1); //yes I could have done this for the rest, but we ball
        pieceMoves.add(new ChessMoveImpl(myPosition, diagonal_right_pos));
        //7. one space down --> col, row-1
        ChessPosition down_pos = new ChessPositionImpl(col, row - 1);
        pieceMoves.add(new ChessMoveImpl(myPosition, down_pos));
        //8. one space to the left and down --> col-1, row-1
        diagonal_left_pos = new ChessPositionImpl(col - 1, row - 1);
        pieceMoves.add(new ChessMoveImpl(myPosition, diagonal_left_pos));

        //Filter out of bounds and if a piece is in my way
        pieceMoves.removeIf(move -> {
            int movCol = move.getEndPosition().getColumn();
            int rowCol = move.getEndPosition().getRow();

            boolean teammateInMyWay = board.getPiece(move.getEndPosition()) != null
                    && board.getPiece(move.getEndPosition()).getTeamColor() == this.getTeamColor();

            return (movCol > 8 || movCol <= 0 || rowCol > 8 || rowCol <= 0) || teammateInMyWay;
        });


        return pieceMoves;
    }

    @Override
    public char getPieceChar() {
        return 'k'; //we'll use k for king and n for "knight"
    }
}
