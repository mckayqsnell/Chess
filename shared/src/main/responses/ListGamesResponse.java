package responses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * the response to a  listGamesRequest
 */
public class ListGamesResponse extends ResponseParent {
    private final Set<GameInfo> games;

    public ListGamesResponse() {
        games = new HashSet<>();
    }

    public void addGame(int gameID, String gameName, String whiteUsername, String blackUsername) {
        games.add(new GameInfo(gameID, gameName, whiteUsername, blackUsername));
    }

    public Set<GameInfo> getGames() {
        return games;
    }

    /*This is just for ServiceTests */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ListGamesResponse that = (ListGamesResponse) obj;
        return Objects.equals(games, that.games);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.games);
    }

    @Override
    public String toString() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        return gson.toJson(games);
    }

    public static class GameInfo {
        private final Integer gameID;
        private final String whiteUsername;
        private final String blackUsername;
        private final String gameName;

        public GameInfo(int gameID, String gameName, String whiteUsername, String blackUsername) {
            this.gameID = gameID;
            this.gameName = gameName;
            this.whiteUsername = whiteUsername;
            this.blackUsername = blackUsername;
        }

        /*This is just for ServiceTests */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            GameInfo gameInfo = (GameInfo) obj;

            return gameID.equals(gameInfo.gameID) &&
                    Objects.equals(whiteUsername, gameInfo.whiteUsername) &&
                    Objects.equals(blackUsername, gameInfo.blackUsername) &&
                    Objects.equals(gameName, gameInfo.gameName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(gameID, whiteUsername, blackUsername, gameName);
        }

        public Integer getGameID() {
            return gameID;
        }

        public String getWhiteUsername() {
            return whiteUsername;
        }

        public String getBlackUsername() {
            return blackUsername;
        }

        public String getGameName() {
            return gameName;
        }
    }
}
