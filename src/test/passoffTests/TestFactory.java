package passoffTests;

import chess.*;

/**
 * Used for testing your code
 * Add in code using your classes for each method for each FIXME
 */
public class TestFactory {

    //Chess Functions
    //------------------------------------------------------------------------------------------------------------------
    public static ChessBoard getNewBoard()
    {
		return new ChessBoardImpl();
    }

    public static ChessGame getNewGame()
    {
		return new ChessGameImpl();
    }

    public static ChessPiece getNewPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type){

        if(type == ChessPiece.PieceType.PAWN)
        {
            return new PawnPiece(pieceColor);
        }
        if(type == ChessPiece.PieceType.ROOK)
        {
            return new RookPiece(pieceColor);
        }
        if(type == ChessPiece.PieceType.KNIGHT)
        {
            return new KnightPiece(pieceColor);
        }
        if(type == ChessPiece.PieceType.BISHOP)
        {
            return new BishopPiece(pieceColor);
        }
        if(type == ChessPiece.PieceType.QUEEN)
        {
            return new QueenPiece(pieceColor);
        }
        if(type == ChessPiece.PieceType.KING)
        {
            return new KingPiece(pieceColor);
        }

		return null;
    }

    public static ChessPosition getNewPosition(Integer row, Integer col)
    {
		return new ChessPositionImpl(col, row);
    }

    public static ChessMove getNewMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece){

		return new ChessMoveImpl(startPosition, endPosition, promotionPiece);
    }
    //------------------------------------------------------------------------------------------------------------------


    //Server API's
    //------------------------------------------------------------------------------------------------------------------
    public static String getServerPort(){
        return "8080";
    }
    //------------------------------------------------------------------------------------------------------------------


    //Websocket Tests
    //------------------------------------------------------------------------------------------------------------------
    public static Long getMessageTime(){
        /*
        Changing this will change how long tests will wait for the server to send messages.
        3000 Milliseconds (3 seconds) will be enough for most computers. Feel free to change as you see fit,
        just know increasing it can make tests take longer to run.
        (On the flip side, if you've got a good computer feel free to decrease it)
         */
        return 3000L;
    }
    //------------------------------------------------------------------------------------------------------------------
}
