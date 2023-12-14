package chess;

import com.google.gson.GsonBuilder;

import java.util.Objects;

public class ChessPositionImpl implements ChessPosition {

    private int column; // 1 - 8 //
    private int row; // 1 - 8 //

    public ChessPositionImpl() {
    }

    public ChessPositionImpl(int column, int row) {
        this.column = column;
        this.row = row;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, row);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;

        ChessPositionImpl pos = (ChessPositionImpl) obj;

        return this.column == pos.column && this.row == pos.row;
    }

    @Override
    public String toString() {
        GsonBuilder builder = new GsonBuilder();
        builder.enableComplexMapKeySerialization();
        builder.registerTypeAdapter(ChessPosition.class, new ChessPositionAdapter());

        /*return String.format("(%d,%d)", column, row); */
        return builder.create().toJson(this);
    }
}
