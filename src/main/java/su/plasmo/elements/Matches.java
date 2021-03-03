package su.plasmo.elements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

@Getter
@AllArgsConstructor
public class Matches {

    @Nullable private VimeUser user;
    private Match[] matches;

    //skipped matches count
    private int offset;

    @Getter
    @AllArgsConstructor
    public static class Match {

        @Nullable private VimeUser user;

        private long id;
        private String game;
        private long date;
        private int duration;

        @Nullable private MatchMap map;

        private JSONObject optionalParams;

        public Match(long id, String game, long date, int duration, MatchMap matchMap, JSONObject optionalParams) {

            this.id = id;
            this.game = game;
            this.date = date;
            this.duration = duration;

            this.map = matchMap;

            this.optionalParams = optionalParams;

        }

        @Getter
        @AllArgsConstructor
        public static class MatchMap {

            private String id;
            private String name;
            private int teams;
            private int playersInTeam;

        }

    }

    /**
     * Отдельный класс создан тут потому что rest-api вайма
     *              выдает разные названия у одних и тех же полей. Именно в матчах. Я не знаю
     *              НАХЕРА они это сделали, но пусть будет так.
     */
    @Getter
    @AllArgsConstructor
    public static class FullMatch {

        private int version;
        private String game;
        private String server;
        private long start;
        private long end;

        @Nullable private String mapName;
        @Nullable private String mapId;

        private JSONObject optionalParams;

    }

}
