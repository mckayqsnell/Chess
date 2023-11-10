package chess;

import java.util.*;

public class ChessGameImpl implements ChessGame {
    private ChessBoard board;
    private TeamColor turn;//set to either white or black depending on whose turn it is

    public ChessGameImpl() {
        board = new ChessBoardImpl();
        board.resetBoard();
        this.turn = TeamColor.WHITE;
    }

    @Override
    public TeamColor getTeamTurn() {
        return turn;
    }

    @Override
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) //Doesn't care whose turn it is
    {
        Set<ChessMove> validMoves = new HashSet<>(getBoard().getPiece(startPosition).pieceMoves(getBoard(), startPosition));
        //Filter
        validMoves.removeIf(chessMove ->
        {
            //save original position for undo
            ChessPosition originalPosition = chessMove.getStartPosition();
            ChessPiece capturedPiece = board.getPiece(chessMove.getEndPosition()); //could be null, that's ok
            //make the move
            board.setPieceAtPosition(chessMove.getEndPosition(), board.getPiece(chessMove.getStartPosition()));
            //set the previous position to null
            board.setPieceAtPosition(chessMove.getStartPosition(), null);

            //see if I'm in check
            boolean isInCheck = isInCheck(board.getPiece(chessMove.getEndPosition()).getTeamColor());

            //undo the move we just made
            undoMove(originalPosition, capturedPiece, chessMove);

            return isInCheck;
        });

        return validMoves; //if this is empty it will return null
    }

    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException//deleted throws exception here because I am handling it
    {
        //see if the move they want to make is within the set returned from validMoves based on the piece/postion
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        boolean isValidMove = false;
        for (ChessMove validMove : validMoves) {
            if (move.equals(validMove)) {
                isValidMove = true;
                break;
            }
        }

        //check to see if its this move's pieces turn
        if (board.getPiece(move.getStartPosition()).getTeamColor() != getTeamTurn())
            isValidMove = false;

        if (isValidMove) {
            //update the turn because we are now making a move.
            if (board.getPiece(move.getStartPosition()).getTeamColor().equals(TeamColor.WHITE))
                setTeamTurn(TeamColor.BLACK);
            if (board.getPiece(move.getStartPosition()).getTeamColor().equals(TeamColor.BLACK))
                setTeamTurn(TeamColor.WHITE);

            //make the move
            board.setPieceAtPosition(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            //change piece to the promotion piece if there is one
            if (move.getPromotionPiece() != null) {
                changeToPromotionPiece(move.getEndPosition(), move.getPromotionPiece(), board.getPiece(move.getEndPosition()).getTeamColor());
            }
            //set the previous position to null
            board.setPieceAtPosition(move.getStartPosition(), null);

        } else {
            //throw exception if that move isn't in the set of moves from validMoves
            throw new InvalidMoveException("INVALID MOVE");
        }
    }

    public void undoMove(ChessPosition originalPosition, ChessPiece capturedPiece, ChessMove move) {
        ChessPiece movedPiece = board.getPiece(move.getEndPosition()); //where that piece is now

        //Set that position where I just moved to null
        board.setPieceAtPosition(move.getEndPosition(), null);
        //now set the originalPosition back to that piece
        board.setPieceAtPosition(originalPosition, movedPiece);

        //If a piece was captured because of the move, then put that back
        if (board.isCaptureMove(move) && capturedPiece != null) {
            board.setPieceAtPosition(move.getEndPosition(), capturedPiece);
        }

    }

    @Override
    public boolean isInCheck(TeamColor teamColor) {
        //Returns true if the specified team’s King could be captured by an opposing piece.
        ChessPosition kingPosition = findKingPosition(teamColor); //Where is the king?

        //get all the possible moves for each piece on the opposing team.
        Collection<ChessPiece> opposingPieces = getAllOpposingPieces(teamColor);

        //check if any opposing piece can capture the king
        for (ChessPiece opposingPiece : opposingPieces) {
            Collection<ChessMove> moves = opposingPiece.pieceMoves(getBoard(), board.getPiecePosition(opposingPiece));

            for (ChessMove move : moves) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true; //The King is in Check
                }
            }
        }
        return false;
    }

    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        //check to see if I'm in check
        if (!isInCheck(teamColor)) {
            return false;
        }

        Collection<ChessPiece> teamPieces = getTeamPieces(teamColor);
        for (ChessPiece piece : teamPieces) {
            Collection<ChessMove> moves = piece.pieceMoves(getBoard(), board.getPiecePosition(piece));
            for (ChessMove move : moves) {
                //save original position for undo
                ChessPosition originalPosition = move.getStartPosition();
                ChessPiece capturedPiece = board.getPiece(move.getEndPosition()); //could be null, that's ok
                //Make the move
                //makeMove(move);
                //make the move
                board.setPieceAtPosition(move.getEndPosition(), board.getPiece(move.getStartPosition()));
                //set the previous position to null
                board.setPieceAtPosition(move.getStartPosition(), null);
                //see if I'm check and return false if I'm not still in check after the move
                if (!isInCheck(teamColor)) {
                    //if the team is not in check then there is at least 1 legal move to get out of check
                    return false;
                }
                //undo the move
                undoMove(originalPosition, capturedPiece, move);
            }
        }

        return true;
    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        //Returns true if the given team has no legal moves, and it is currently that team’s turn.
        if (isInCheck(teamColor)) //if I'm in check then I'm not in stalemate
        {
            return false;
        }

        Collection<ChessPiece> myPieces = getTeamPieces(teamColor);

        for (ChessPiece piece : myPieces) {
            Collection<ChessMove> validMoves = validMoves(board.getPiecePosition(piece));

            if (!validMoves.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /* Private Helper Methods */

    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (Map.Entry<ChessPosition, ChessPiece> entry : board.getBoard().entrySet()) {
            ChessPiece piece = entry.getValue();
            ChessPosition position = entry.getKey();
            if (piece != null)
                if (piece.getTeamColor().equals(teamColor))
                    if (piece.getPieceType().equals(ChessPiece.PieceType.KING))
                        return position;
        }
        return null; //if the king doesn't exist. This shouldn't be reached but in case.
    }

    private Collection<ChessPiece> getAllOpposingPieces(TeamColor teamColor) {
        Collection<ChessPiece> opposingPieces = new ArrayList<>();
        //For every entry in the board, look at each of its pieces teamColors.
        for (Map.Entry<ChessPosition, ChessPiece> entry : board.getBoard().entrySet()) {
            ChessPiece piece = entry.getValue();
            //if that piece exists, then if its teamColor is opposite of mine add it to the list
            if (piece != null) {
                if (piece.getTeamColor() != teamColor) {
                    opposingPieces.add(piece);
                }
            }
        }
        return opposingPieces;
    }

    private Collection<ChessPiece> getTeamPieces(TeamColor teamColor) {
        Collection<ChessPiece> teamPieces = new ArrayList<>();
        //For every entry in the board, look at each of its pieces teamColors.
        for (Map.Entry<ChessPosition, ChessPiece> entry : board.getBoard().entrySet()) {
            ChessPiece piece = entry.getValue();
            //if that piece exists, then if its teamColor is opposite of mine add it to the list
            if (piece != null) {
                if (piece.getTeamColor().equals(teamColor)) {
                    teamPieces.add(piece);
                }
            }
        }
        return teamPieces;
    }

    private void changeToPromotionPiece(ChessPosition position, ChessPiece.PieceType pieceType, TeamColor teamColor) {
        if (pieceType.equals(ChessPiece.PieceType.QUEEN)) {
            board.setPieceAtPosition(position, new QueenPiece(teamColor));
        }
        if (pieceType.equals(ChessPiece.PieceType.ROOK)) {
            board.setPieceAtPosition(position, new RookPiece(teamColor));
        }
        if (pieceType.equals(ChessPiece.PieceType.KNIGHT)) {
            board.setPieceAtPosition(position, new KnightPiece(teamColor));
        }
        if (pieceType.equals(ChessPiece.PieceType.BISHOP)) {
            board.setPieceAtPosition(position, new BishopPiece(teamColor));
        }
    }

    @Override
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    @Override
    public ChessBoard getBoard() {
        return board;
    }
}
