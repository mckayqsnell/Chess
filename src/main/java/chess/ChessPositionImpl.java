package chess;

import java.util.Objects;

public class ChessPositionImpl implements ChessPosition {
    public ChessPositionImpl()
    {
        column  = 0;
        row = 0;
        //System.out.println("FIX CHESS POSITION CONSTRUCTOR"); //TODO
    }
    public ChessPositionImpl(int column, int row)
    {
        this.column = column;
        this.row = row;
    }
    private final int column; // 1 - 8 //
    private final int row; // 1 - 8 //
    @Override
    public int getRow()
    {
        return row;
    }

    @Override
    public int getColumn()
    {
        return column;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(column,row);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj) return true;
        if(obj == null || this.getClass() != obj.getClass()) return false;

        ChessPositionImpl pos = (ChessPositionImpl)obj;

        return this.column == pos.column && this.row == pos.row;
    }

    @Override
    public String toString()
    {
        return String.format("(%d, %d)", column, row); //returns the cords(column, row)
    }
}
