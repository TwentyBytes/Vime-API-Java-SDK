package su.plasmo.elements;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Matches {

    @Nullable
    VimeUser user;
    Match[] matches;

    //skipped matches count
    int offset;

    @Getter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Match {

        @Nullable
        VimeUser user;

        long id;
        String game;
        long date;
        int duration;

        @Nullable
        MatchMap map;

        JSONObject optionalParams;

        public Match(long id, String game, long date, int duration, @Nullable MatchMap matchMap, JSONObject optionalParams) {
            this.id = id;
            this.game = game;
            this.date = date;
            this.duration = duration;

            this.map = matchMap;

            this.optionalParams = optionalParams;
        }

        @Getter
        @AllArgsConstructor
        @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
        public static class MatchMap {

            String id;
            String name;
            int teams;
            int playersInTeam;

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
