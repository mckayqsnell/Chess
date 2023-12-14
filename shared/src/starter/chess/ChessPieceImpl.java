package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

public abstract class ChessPieceImpl implements ChessPiece { //abstract means you can't create an instance of it, but you can make subclasses that can have instances
    private final ChessGame.TeamColor teamColor;

    private final PieceType pieceType;

    /*Methods to be inherited */

    public ChessPieceImpl(ChessGame.TeamColor teamColor, PieceType pieceType) {
        this.teamColor = teamColor;
        this.pieceType = pieceType;
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /*Abstract Methods to be implemented in subclasses */

    @Override
    public PieceType getPieceType() {
        return pieceType;
    } //For each of the subclasses to implement


    @Override
    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition); //For each of the subclasses to implement
    //Each piece will return its corresponding letter(for toString in Board)

    public abstract String getPieceChar();


    /* Additional helper methods to be inherited */

    //Diagonals methods for Bishop and Queen
    public Set<ChessMove> diagonalMoves(ChessBoard board, ChessPosition myPosition) {
        Set<ChessMove> diagonalMoves = new HashSet<>();

        int col = myPosition.getColumn();
        int row = myPosition.getRow();
        int distance_column = col > 1 ? col - 1 : 0;
        int distance_row = 8 - row;
        int distance_diagonal = (int) Math.sqrt(distance_column * distance_column + distance_row * distance_row);
        for (int i = 0; i < distance_diagonal; i++) {
            col--;
            row++;
            ChessPosition newPosition = new ChessPositionImpl(col, row);
            boolean pieceInTheWay = board.getPiece(newPosition) != null;
            if (row > 8 || row < 1 || col > 8 || col < 1) {
                break;
            } else if (pieceInTheWay) {
                if (board.getPiece(newPosition).getTeamColor() != this.getTeamColor()) {
                    diagonalMoves.add(new ChessMoveImpl(myPosition, newPosition)); //not on my team, so capture move
                }
                break;
            }
            //regular add
            diagonalMoves.add(new ChessMoveImpl(myPosition, newPosition));
        }

        /*2nd diagonal going down and to the left (-,-) */
        col = myPosition.getColumn();
        row = myPosition.getRow();
        distance_column = col > 1 ? col - 1 : 0;
        distance_row = row > 1 ? row - 1 : 0;
        distance_diagonal = (int) Math.sqrt(distance_column * distance_column + distance_row * distance_row);
        for (int i = 0; i < distance_diagonal; i++) {
            col--;
            row--;
            ChessPosition newPosition = new ChessPositionImpl(col, row);
            boolean pieceInTheWay = board.getPiece(newPosition) != null;
            if (row > 8 || row < 1 || col > 8 || col < 1) {
                break;
            } else if (pieceInTheWay) {
                if (board.getPiece(newPosition).getTeamColor() != this.getTeamColor()) {
                    diagonalMoves.add(new ChessMoveImpl(myPosition, newPosition)); //not on my team, so capture move
                }
                break;
            }
            //regular add
            diagonalMoves.add(new ChessMoveImpl(myPosition, newPosition));
        }

        /* Going down and to the right(+,-) */
        col = myPosition.getColumn();
        row = myPosition.getRow();
        distance_column = col - 8;
        distance_row = row > 1 ? row - 1 : 0;
        distance_diagonal = (int) Math.sqrt(distance_column * distance_column + distance_row * distance_row);
        for (int i = 0; i < distance_diagonal; i++) {
            col++;
            row--;
            ChessPosition newPosition = new ChessPositionImpl(col, row);
            boolean pieceInTheWay = board.getPiece(newPosition) != null;
            if (row > 8 || row < 1 || col > 8 || col < 1) {
                break;
            } else if (pieceInTheWay) {
                if (board.getPiece(newPosition).getTeamColor() != this.getTeamColor()) {
                    diagonalMoves.add(new ChessMoveImpl(myPosition, newPosition)); //not on my team, so capture move
                }
                break;
            }
            //regular add
            diagonalMoves.add(new ChessMoveImpl(myPosition, newPosition));
        }

        /* Going up and to the right(+,+) */
        col = myPosition.getColumn();
        row = myPosition.getRow();
        distance_column = col - 8;
        distance_row = row - 8;
        distance_diagonal = (int) Math.sqrt(distance_column * distance_column + distance_row * distance_row);
        for (int i = 0; i < distance_diagonal; i++) {
            col++;
            row++;
            ChessPosition newPosition = new ChessPositionImpl(col, row);
            boolean pieceInTheWay = board.getPiece(newPosition) != null;
            if (row > 8 || row < 1 || col > 8 || col < 1) {
                break;
            } else if (pieceInTheWay) {
                if (board.getPiece(newPosition).getTeamColor() != this.getTeamColor()) {
                    diagonalMoves.add(new ChessMoveImpl(myPosition, newPosition)); //not on my team, so capture move
                }
                break;
            }
            //regular add
            diagonalMoves.add(new ChessMoveImpl(myPosition, newPosition));
        }

        return diagonalMoves;
    }

    //For Rook and Queen
    public Set<ChessMove> rectilinearMoves(ChessBoard board, ChessPosition myPosition) {
        Set<ChessMove> rectilinearMoves = new HashSet<>();

        /*4 loops for each direction on each axis */

        //up (white's perspective) (y-axis) --> only update row
        int col = myPosition.getColumn(); //set column, must reset before every loop
        int row = myPosition.getRow(); //set row, must reset before every loop
        int distance_edge = 8 - row; //8 is the max of the y-axis
        for (int i = 0; i < distance_edge; i++) {
            row++;
            ChessPosition newPosition = new ChessPositionImpl(col, row);
            boolean pieceInTheWay = board.getPiece(newPosition) != null;
            if (row > 8 || row < 1 || col > 8 || col < 1) {
                break;
            } else if (pieceInTheWay) {
                if (board.getPiece(newPosition).getTeamColor() != this.getTeamColor()) {
                    rectilinearMoves.add(new ChessMoveImpl(myPosition, newPosition)); //not on my team, so capture move
                }
                break;
            }
            //regular add
            rectilinearMoves.add(new ChessMoveImpl(myPosition, newPosition));
        }

        //down y-axis --> only update row
        col = myPosition.getColumn(); //reset column
        row = myPosition.getRow(); //reset row
        distance_edge = row > 1 ? row - 1 : 0; // 1 is the min of the y-axis
        for (int i = 0; i < distance_edge; i++) {
            row--;
            ChessPosition newPosition = new ChessPositionImpl(col, row);
            boolean pieceInTheWay = board.getPiece(newPosition) != null;
            if (row > 8 || row < 1 || col > 8 || col < 1) {
                break;
            } else if (pieceInTheWay) {
                if (board.getPiece(newPosition).getTeamColor() != this.getTeamColor()) {
                    rectilinearMoves.add(new ChessMoveImpl(myPosition, newPosition)); //not on my team, so capture move
                }
                break;
            }
            //regular add
            rectilinearMoves.add(new ChessMoveImpl(myPosition, newPosition));
        }

        //to the right x-axis --> only update col
        col = myPosition.getColumn(); //reset column
        row = myPosition.getRow(); //reset row
        distance_edge = 8 - col; // 8 is the max of the x-axis
        for (int i = 0; i < distance_edge; i++) {
            col++;
            ChessPosition newPosition = new ChessPositionImpl(col, row);
            boolean pieceInTheWay = board.getPiece(newPosition) != null;
            if (col > 8 || col < 1 || row > 8 || row < 1) {
                break;
            } else if (pieceInTheWay) {
                if (board.getPiece(newPosition).getTeamColor() != this.getTeamColor()) {
                    rectilinearMoves.add(new ChessMoveImpl(myPosition, newPosition)); //not on my team, so capture move
                }
                break;
            }
            //regular add
            rectilinearMoves.add(new ChessMoveImpl(myPosition, newPosition));
        }

        //to the left x-axis --> only update col
        col = myPosition.getColumn(); //reset column
        row = myPosition.getRow(); //reset row
        distance_edge = col > 1 ? col - 1 : 0; // 1 is the min of the x-axis
        for (int i = 0; i < distance_edge; i++) {
            col--;
            ChessPosition newPosition = new ChessPositionImpl(col, row);
            boolean pieceInTheWay = board.getPiece(newPosition) != null;
            if (col > 8 || col < 1 || row > 8 || row < 1) {
                break;
            } else if (pieceInTheWay) {
                if (board.getPiece(newPosition).getTeamColor() != this.getTeamColor()) {
                    rectilinearMoves.add(new ChessMoveImpl(myPosition, newPosition)); //not on my team, so capture move
                }
                break;
            }
            //regular add
            rectilinearMoves.add(new ChessMoveImpl(myPosition, newPosition));
        }

        return rectilinearMoves;
    }

    @Override
    public String toString() {
        return this.pieceType + ":" + this.teamColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPieceImpl that = (ChessPieceImpl) o;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }
}
