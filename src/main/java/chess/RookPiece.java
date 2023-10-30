package chess;

import java.util.Collection;

public class RookPiece extends ChessPieceImpl{
    public RookPiece(ChessGame.TeamColor teamColor) {
        super(teamColor);
    }

    @Override
    public PieceType getPieceType()
    {
        return PieceType.ROOK;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition)
    {
        return rectilinearMoves(board, myPosition);
    }

    @Override
    public char getPieceChar()
    {
        return 'r';
    }
}