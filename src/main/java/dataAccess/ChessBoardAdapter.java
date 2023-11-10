package dataAccess;

import chess.ChessBoard;
import chess.ChessBoardImpl;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessPositionImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Map;

public class ChessBoardAdapter extends TypeAdapter<ChessBoard> {
    @Override
    public void write(JsonWriter jsonWriter, ChessBoard chessBoard) throws IOException {
        System.out.println("I made it to the board adapter!");
        jsonWriter.value(chessBoard.toString());
    }

    @Override
    public ChessBoard read(JsonReader jsonReader) throws IOException {
        return new ChessBoardImpl();
    }
}
