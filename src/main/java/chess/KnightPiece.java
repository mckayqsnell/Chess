package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class KnightPiece extends ChessPieceImpl{
    public KnightPiece(ChessGame.TeamColor teamColor) {
        super(teamColor);
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.KNIGHT;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition)
    {
        //Knights move in L shapes.
        Set<ChessMove> pieceMoves = new HashSet<>();
        int col = myPosition.getColumn();
        int row = myPosition.getRow();

        /* Add all possible moves without taking into account out of bounds and pieces in the way*/

        // +2 row, +1 col --> upside down L going to the right. So up 2 rows and over 1 column to the right.
        ChessPosition newPosition1 = new ChessPositionImpl(col+1, row+2);
        pieceMoves.add(new ChessMoveImpl(myPosition, newPosition1));

        //+2 row, -1 col --> upside down L going to the left. So up +2 rows and over -1 column to the left.
        ChessPosition newPosition2 = new ChessPositionImpl( col-1, row+2);
        pieceMoves.add(new ChessMoveImpl(myPosition, newPosition2));

        //+1 row, +2 col --> up +1 row, over to the right +2 col
        ChessPosition newPosition3 = new ChessPositionImpl( col+2, row+1);
        pieceMoves.add(new ChessMoveImpl(myPosition, newPosition3));

        //+1 row, -2 col --> up +1 row, over to the left -2 col
        ChessPosition newPosition4 = new ChessPositionImpl( col-2, row+1);
        pieceMoves.add(new ChessMoveImpl(myPosition, newPosition4));

        //-1 row, +2 col --> down -1 row, over to the right +2 col
        ChessPosition newPosition5 = new ChessPositionImpl( col+2, row-1);
        pieceMoves.add(new ChessMoveImpl(myPosition, newPosition5));

        //-1 row, -2 col --> down -1 row, over to the left -2 col
        ChessPosition newPosition6 = new ChessPositionImpl( col-2, row-1);
        pieceMoves.add(new ChessMoveImpl(myPosition, newPosition6));

        //-2 row, +1 col --> down -2 row, over to the right +1 col
        ChessPosition newPosition7 = new ChessPositionImpl( col+1, row-2);
        pieceMoves.add(new ChessMoveImpl(myPosition, newPosition7));

        //-2 row, -1 col --> down -2 row, over to the left -1 col
        ChessPosition newPosition8 = new ChessPositionImpl(col-1, row-2);
        pieceMoves.add(new ChessMoveImpl(myPosition, newPosition8));

        /* Filter out the moves that are out of bounds or in my way */
        //lambda expression to remove items from the set based on out-of-bounds or in my way
        pieceMoves.removeIf(move -> {
            int movCol = move.getEndPosition().getColumn();
            int rowCol = move.getEndPosition().getRow();

            boolean teammateInMyWay = board.getPiece(move.getEndPosition()) != null
                    && board.getPiece(move.getEndPosition()).getTeamColor() == this.getTeamColor();

            return (movCol > 8 || movCol <=0 || rowCol > 8 || rowCol <= 0) || teammateInMyWay;
        });

        return pieceMoves;
    }

    @Override
    public char getPieceChar()
    {
        return 'n'; //using n for knight because of the king
    }
}
