package chess;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ChessPieceAdapter extends TypeAdapter<ChessPiece> {
    @Override
    public void write(JsonWriter jsonWriter, ChessPiece piece) throws IOException {
        Gson gson = new Gson();

        switch (piece.getPieceType()) {
            case BISHOP -> gson.getAdapter(BishopPiece.class).write(jsonWriter, (BishopPiece) piece);
            case KNIGHT -> gson.getAdapter(KnightPiece.class).write(jsonWriter, (KnightPiece) piece);
            case PAWN -> gson.getAdapter(PawnPiece.class).write(jsonWriter, (PawnPiece) piece);
            case KING -> gson.getAdapter(KingPiece.class).write(jsonWriter, (KingPiece) piece);
            case QUEEN -> gson.getAdapter(QueenPiece.class).write(jsonWriter, (QueenPiece) piece);
            case ROOK -> gson.getAdapter(RookPiece.class).write(jsonWriter, (RookPiece) piece);
        }
    }

    @Override
    public ChessPiece read(JsonReader jsonReader) throws IOException {
        ChessGame.TeamColor teamColor = null;
        ChessPiece.PieceType pieceType = null;

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "teamColor" -> teamColor = ChessGame.TeamColor.valueOf(jsonReader.nextString());
                case "pieceType" -> pieceType = ChessPiece.PieceType.valueOf(jsonReader.nextString());
            }
        }

        jsonReader.endObject();

        if (pieceType == null) {
            return null;
        } else {
            return switch (pieceType) {
                case BISHOP -> new BishopPiece(teamColor);
                case KNIGHT -> new KnightPiece(teamColor);
                case PAWN -> new PawnPiece(teamColor);
                case KING -> new KingPiece(teamColor);
                case QUEEN -> new QueenPiece(teamColor);
                case ROOK -> new RookPiece(teamColor);
            };
        }
    }
}
