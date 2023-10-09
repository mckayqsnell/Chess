package chess;

import java.util.Collection;
import java.util.Set;

public class QueenPiece extends ChessPieceImpl{
    public QueenPiece(ChessGame.TeamColor teamColor) {
        super(teamColor);
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.QUEEN;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition)
    {
        //combine diagonal and rectilinear moves
        Set<ChessMove> allMoves = diagonalMoves(board, myPosition);
        allMoves.addAll(rectilinearMoves(board, myPosition));
        return allMoves;
    }

    @Override
    public char getPieceChar() {
        return 'q';
    }
}
