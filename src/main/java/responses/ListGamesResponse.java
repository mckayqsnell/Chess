package responses;

import java.util.ArrayList;
import java.util.Objects;

/**
 * the response to a  listGamesRequest
 */
public class ListGamesResponse extends ResponseParent {
    private final ArrayList<GameInfo> games;

    public ListGamesResponse() {
        games = new ArrayList<>();
    }

    public void addGame(int gameID, String gameName, String whiteUsername, String blackUsername) {
        games.add(new GameInfo(gameID, gameName, whiteUsername, blackUsername));
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
    }
}
