package dataAccess;

import chess.ChessBoard;
import chess.ChessBoardImpl;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ChessBoardAdapter extends TypeAdapter<ChessBoard> {
    @Override
    public void write(JsonWriter jsonWriter, ChessBoard chessBoard) throws IOException {
        jsonWriter.value(chessBoard.toString());
    }

    @Override
    public ChessBoard read(JsonReader jsonReader) throws IOException {
        String s = jsonReader.nextString();
        //System.out.println("in ChessBoard Read" + s);
        GsonBuilder builder = new GsonBuilder();
        builder.enableComplexMapKeySerialization();
        builder.registerTypeAdapter(ChessPosition.class, new ChessPositionAdapter());
        builder.registerTypeAdapter(ChessPiece.class, new ChessPieceAdapter());
        Gson gson = builder.create();

        return gson.fromJson(s, ChessBoardImpl.class);
    }
}
