package dataAccess;

import chess.ChessPosition;
import chess.ChessPositionImpl;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ChessPositionAdapter extends TypeAdapter<ChessPosition> {
    @Override
    public void write(JsonWriter jsonWriter, ChessPosition position) throws IOException {
        jsonWriter.value(position.toString());
    }

    @Override
    public ChessPosition read(JsonReader jsonReader) throws IOException {
        System.out.println("in.nextString: " + jsonReader.nextString());
        return new ChessPositionImpl(jsonReader.nextInt(), jsonReader.nextInt());
    }
}
