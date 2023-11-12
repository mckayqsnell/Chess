package dataAccess;

import chess.ChessPosition;
import chess.ChessPositionImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ChessPositionAdapter extends TypeAdapter<ChessPosition> {
    @Override
    public void write(JsonWriter jsonWriter, ChessPosition position) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessPosition.class, new ChessPositionAdapter());
        builder.enableComplexMapKeySerialization();
        //System.out.println("In the write method for ChessPosAdapter");

        Gson gson = builder.create();
        jsonWriter.value(gson.toJson(position));
    }

    @Override
    public ChessPosition read(JsonReader jsonReader) throws IOException {
        String s = jsonReader.nextString();
        //System.out.println("in.nextString: " + s);
        GsonBuilder builder = new GsonBuilder();
        builder.enableComplexMapKeySerialization();
        builder.registerTypeAdapter(ChessPosition.class, new ChessPositionAdapter());
        Gson gson = builder.create();

        return gson.fromJson(s, ChessPositionImpl.class);
    }
}
