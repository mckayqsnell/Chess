package chess;

import java.util.Objects;

public class ChessMoveImpl implements ChessMove {
    private ChessPosition startPosition;
    private ChessPosition endPosition;
    private ChessPiece.PieceType promotionPiece;

    public ChessMoveImpl()
    {
        //TODO: Not sure what to put here. Dr.Wilkerson said we need no argument constructors for JSON SERIALIZATION
    }
    public ChessMoveImpl(ChessPosition start, ChessPosition end) //For all pieces that aren't pawns
    {
        this.startPosition = start;
        this.endPosition = end;
    }

    public ChessMoveImpl(ChessPosition start, ChessPosition end, ChessPiece.PieceType promotionPiece) //For pawns
    {
        this.startPosition = start;
        this.endPosition = end;
        this.promotionPiece = promotionPiece;
    }
    @Override
    public ChessPosition getStartPosition()
    {
        return startPosition;
    }

    @Override
    public ChessPosition getEndPosition()
    {
        return endPosition;
    }

    @Override
    public ChessPiece.PieceType getPromotionPiece()
    {
        return promotionPiece;
    }

    @Override
    public String toString()
    {
        return String.format("%s -> %s", startPosition.toString(), endPosition.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition.getColumn(), startPosition.getRow(), endPosition.getColumn(), endPosition.getRow());
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj) return true;
        if(obj == null || this.getClass() != obj.getClass()) return false;

        ChessMoveImpl mov = (ChessMoveImpl) obj;

        //Return the result of checking if start positions and end positions both match
        return this.getStartPosition().equals(mov.getStartPosition()) && this.getEndPosition().equals(mov.getEndPosition());
    }
}
