package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PawnPiece extends ChessPieceImpl {
    public PawnPiece(ChessGame.TeamColor teamColor) {
        super(teamColor, PieceType.PAWN); //calls the parent class constructor for teamColor. Still its own separate thing.
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Set<ChessMove> pieceMoves = new HashSet<>();
        int col = myPosition.getColumn();
        int row = myPosition.getRow();

        //Single Moves
        //Ponds can't move backwards(based on your team)
        ChessPosition new_single_position = new ChessPositionImpl();
        if (getTeamColor() == ChessGame.TeamColor.WHITE)
            new_single_position = new ChessPositionImpl(col, row + 1);
        else if (getTeamColor() == ChessGame.TeamColor.BLACK)
            new_single_position = new ChessPositionImpl(col, row - 1);
        //Check to see if any piece ,regardless of team, is in my way
        if (board.getPiece(new_single_position) == null) {
            ChessMove mov = new ChessMoveImpl(myPosition, new_single_position);
            pieceMoves.add(mov);
        }

        //Double Move, if on 1st move which means if my position is in row 2 for White or row 7 for black
        int starter_row_white = 2;
        int starter_row_black = 7;
        ChessPosition new_dub_Position;
        if (myPosition.getRow() == starter_row_white && getTeamColor() == ChessGame.TeamColor.WHITE) {
            new_dub_Position = new ChessPositionImpl(col, row + 2);
            if (board.getPiece(new_dub_Position) == null && board.getPiece(new_single_position) == null) {
                ChessMove mov = new ChessMoveImpl(myPosition, new_dub_Position);
                pieceMoves.add(mov);
            }
        } else if (myPosition.getRow() == starter_row_black && getTeamColor() == ChessGame.TeamColor.BLACK) {
            new_dub_Position = new ChessPositionImpl(col, row - 2);
            if (board.getPiece(new_dub_Position) == null && board.getPiece(new_single_position) == null) {
                ChessMove mov = new ChessMoveImpl(myPosition, new_dub_Position);
                pieceMoves.add(mov);
            }
        }

        //Capture option for a pawn
        //White diagonals
        if (getTeamColor() == ChessGame.TeamColor.WHITE) {
            ChessPosition diagonal_left = new ChessPositionImpl(col - 1, row + 1);
            if (board.getPiece(diagonal_left) != null && board.getPiece(diagonal_left).getTeamColor() == ChessGame.TeamColor.BLACK) //pretty sure checking if black will be safe
            {
                //if that space is occupied, and they aren't on my team then add this capture to the left as a move
                ChessMove captureLeft = new ChessMoveImpl(myPosition, diagonal_left);
                pieceMoves.add(captureLeft);
            }
            ChessPosition diagonal_right = new ChessPositionImpl(col + 1, row + 1);
            if (board.getPiece(diagonal_right) != null && board.getPiece(diagonal_right).getTeamColor() == ChessGame.TeamColor.BLACK) {
                ChessMove captureRight = new ChessMoveImpl(myPosition, diagonal_right);
                pieceMoves.add(captureRight);
            }
        }
        //Black diagonals
        else if (getTeamColor() == ChessGame.TeamColor.BLACK) //From White's perspective
        {
            ChessPosition diagonal_left = new ChessPositionImpl(col - 1, row - 1);
            if (board.getPiece(diagonal_left) != null && board.getPiece(diagonal_left).getTeamColor() == ChessGame.TeamColor.WHITE) //pretty sure checking if White will be safe
            {
                //if that space is occupied, and they aren't on my team then add this capture to the left as a move
                ChessMove captureLeft = new ChessMoveImpl(myPosition, diagonal_left);
                pieceMoves.add(captureLeft);
            }
            ChessPosition diagonal_right = new ChessPositionImpl(col + 1, row - 1);
            if (board.getPiece(diagonal_right) != null && board.getPiece(diagonal_right).getTeamColor() == ChessGame.TeamColor.WHITE) {
                ChessMove captureRight = new ChessMoveImpl(myPosition, diagonal_right);
                pieceMoves.add(captureRight);
            }
        }

        return pieceMoves;
    }

    @Override
    public String getPieceChar() {
        return "p";
    }
}
