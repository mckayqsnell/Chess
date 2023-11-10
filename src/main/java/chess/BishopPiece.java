package chess;

import java.util.Collection;

public class BishopPiece extends ChessPieceImpl {
    public BishopPiece(ChessGame.TeamColor teamColor) {
        super(teamColor, PieceType.BISHOP);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return diagonalMoves(board, myPosition);
    }

    @Override
    public char getPieceChar() {
        return 'b';
    }
}
