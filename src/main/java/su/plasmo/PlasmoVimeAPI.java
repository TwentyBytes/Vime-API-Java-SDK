package su.plasmo;

import lombok.Getter;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import su.plasmo.elements.*;
import su.plasmo.throwables.APICallException;

import java.util.HashMap;
import java.util.Map;

public class PlasmoVimeAPI {

    public static final String API_URL = "https://api.vimeworld.ru/";

    private String token;

    private CloseableHttpClient client = HttpClients.createDefault();

    @Getter
    private Limit limit = new Limit();

    public PlasmoVimeAPI(String token) {
        this.token = token;
    }

    public static PlasmoVimeAPI construct(String token) {
        return new PlasmoVimeAPI(token);
    }

    public static PlasmoVimeAPI createNewApi(String token) {
        return construct(token);
    }

    public static PlasmoVimeAPI construct() {
        return construct(null);
    }

    public static PlasmoVimeAPI createNewApi() {
        return construct(null);
    }

    public VimeUser getUser(int id) {

        String answer = getRequest("user/" + id);

        JSONTokener tokener = new JSONTokener(answer);
        JSONArray array = new JSONArray(tokener);
        JSONObject object = array.optJSONObject(0);

        return parseUser(object);

    }

    public VimeUser getUser(String name) {

        String answer = getRequest("user/name/" + name);

        JSONTokener tokener = new JSONTokener(answer);
        JSONArray array = new JSONArray(tokener);

        return parseUser(array.optJSONObject(0));

    }

    public VimeUser[] getUsers(int... ids) {

        if (ids.length > 1000)
            throw new IllegalArgumentException("Players amount can`t be > 1000");

        VimeUser[] users = new VimeUser[ids.length];
        if (ids.length < 1)
            return users;

        JSONArray array = new JSONArray();

        for (int id : ids) {

            array.put(id);

        }

        HttpPost post = new HttpPost(API_URL + "user/session" + (this.token == null ? "" : "?token=" + this.token));

        try {

            post.setEntity(new StringEntity(array.toString()));

        } catch (Throwable throwable) {

            throwable.printStackTrace();

        }

        String answer = requestToString(post);

        JSONTokener tokener = new JSONTokener(answer);
        JSONArray userObjectsArray = new JSONArray(tokener);

        for (int index = 0, length = users.length; index < length; index++) {

            users[index] = parseUser(userObjectsArray.optJSONObject(index));

        }

        return users;

    }

    public VimeUser[] getUsers(String... names) {

        if (names.length > 50)
            throw new IllegalArgumentException("Max users amount 50");
        else if (names.length < 1)
            throw new IllegalArgumentException("Min users amount 1");

        StringBuilder builder = new StringBuilder();

        for (int index = 0, length = names.length; index < length; index++) {

            builder.append(names[index]);
            if (index != length - 1)
                builder.append(",");

        }


        String answer = getRequest("user/name/" + builder.toString());

        JSONTokener tokener = new JSONTokener(answer);
        JSONArray array = new JSONArray(tokener);
        int length = array.length();
        VimeUser[] users = new VimeUser[length];

        for (int index = 0; index < length; index++) {

            users[index] = parseUser(array.optJSONObject(index));

        }

        return users;

    }

    public VimeUser[] getOnlineStaff() {

        String answer = getRequest("/online/staff");

        JSONTokener tokener = new JSONTokener(answer);
        JSONArray array = new JSONArray(tokener);
        int length = array.length();
        VimeUser[] users = new VimeUser[length];
        if (array.isEmpty())
            return users;

        for (int index = 0; index < length; index++) {

            users[index] = parseUser(array.optJSONObject(index));

        }

        return users;

    }

    public Matches.FullMatch getMatch(int matchID) {

        String answer = getRequest("/match/" + matchID);

        JSONTokener tokener = new JSONTokener(answer);
        JSONObject object = new JSONObject(tokener);

        return new Matches.FullMatch(object.optInt("version"), object.optString("game"), object.optString("server"), object.optLong("start"), object.optLong("end"), object.optString("mapName"), object.optString("mapId"), object);

    }

    public Matches getUserMatches(int id) {

        return getUserMatches(id, 20, 0);

    }

    public Matches getUserMatches(int id, int amount) {

        return getUserMatches(id, amount, 0);

    }

    @Nullable
    public Matches getUserMatches(int id, int amount, int offset) {

        if (amount > 50)
            throw new IllegalArgumentException("Max amount = 50");
        else if (amount < 1)
            throw new IllegalArgumentException("Min amount = 1");

        if (offset > 2000)
            throw new IllegalArgumentException("Max offset = 2000");

        String answer = getRequest("/user/" + id + "/matches");

        JSONTokener tokener = new JSONTokener(answer);
        JSONObject object = new JSONObject(tokener);

        VimeUser user = parseUser(object.optJSONObject("user"));
        int skipped = object.optJSONObject("request").optInt("offset");

        JSONArray array = object.optJSONArray("matches");

        int length = array.length();
        if (length < 1)
            return null;

        Matches.Match[] requestedMatches = new Matches.Match[length];

        for (int index = 0; index < length; index++) {

            JSONObject match = array.optJSONObject(index);

            JSONObject map = match.optJSONObject("map");
            Matches.Match.MatchMap matchMap = null;
            if (map != null) {

                matchMap = new Matches.Match.MatchMap(map.optString("id"), map.optString("name"), map.optInt("teams"), map.optInt("playersInTeam"));

            }

            requestedMatches[index] = new Matches.Match(user, match.optLong("id"), match.optString("game"), match.optLong("date"), match.optInt("duration"), matchMap, match);

        }

        return new Matches(user, requestedMatches, skipped);

    }

    @Nullable
    public Achievements getAchievements(int id) {

        String answer = getRequest("user/" + id + "/achievements");

        JSONTokener tokener = new JSONTokener(answer);
        JSONObject object = new JSONObject(tokener);

        JSONObject userObject = object.optJSONObject("user");
        VimeUser user = parseUser(userObject);

        JSONArray achievementsArray = object.optJSONArray("achievements");
        int length = achievementsArray.length();

        if (achievementsArray.isEmpty())
            return null;

        Achievements.Achievement[] achievements = new Achievements.Achievement[length];

        for (int index = 0; index < length; index++) {

            JSONObject achievement = achievementsArray.optJSONObject(index);

            achievements[index] = new Achievements.Achievement(achievement.optInt("id"), achievement.optLong("time"));

        }

        return new Achievements(user, achievements);

    }

    public Achievements getAchievements(VimeUser user) {

        return getAchievements(user.getId());

    }

    public Achievements.List getAchievementsList() {

        String answer = getRequest("/misc/achievements");

        JSONTokener tokener = new JSONTokener(answer);
        JSONObject object = new JSONObject(tokener);

        JSONArray keys = object.names();

        //int = achievement id.
        Map<Integer, Achievements.List.ListAchievement> achievements = new HashMap<>();

        for (int index = 0, length = keys.length(); index < length; index++) {

            JSONArray achievementsArray = object.optJSONArray(keys.optString(index));

            for (int achievementIndex = 0, achievementsLength = achievementsArray.length(); achievementIndex < achievementsLength; achievementIndex++) {

                JSONObject achievement = achievementsArray.optJSONObject(achievementIndex);
                int id = achievement.optInt("id");

                JSONArray descriptionArray = achievement.optJSONArray("description");
                int descriptionLength = descriptionArray.length();
                String[] description = new String[descriptionLength];

                for (int lineIndex = 0; lineIndex < descriptionLength; lineIndex++) {

                    description[lineIndex] = descriptionArray.optString(lineIndex);

                }

                achievements.put(id, new Achievements.List.ListAchievement(id, achievement.optString("title"), achievement.optInt("reward"), description));

            }

        }

        return new Achievements.List(achievements);

    }

    @Nullable
    public Friends getUserFriends(int id) {

        String answer = getRequest("user/" + id + "/friends");

        JSONTokener tokener = new JSONTokener(answer);
        JSONObject object = new JSONObject(tokener);

        JSONObject userObject = object.optJSONObject("user");
        VimeUser user = parseUser(userObject);

        JSONArray array = object.optJSONArray("friends");
        int length = array.length();
        VimeUser[] users = new VimeUser[length];

        if (array.isEmpty())
            return null;

        for (int index = 0; index < length; index++) {

            users[index] = parseUser(array.optJSONObject(index));

        }

        return new Friends(user, users);

    }

    public Friends getUserFriends(VimeUser user) {

        return getUserFriends(user.getId());

    }

    @Nullable
    public LeaderBoard.UserLeaderBoards getUserLeaderBoards(int id) {

        String answer = getRequest("user/" + id + "/leaderboards");

        JSONTokener tokener = new JSONTokener(answer);
        JSONObject object = new JSONObject(tokener);

        JSONObject userObject = object.optJSONObject("user");
        VimeUser user = parseUser(userObject);

        JSONArray array = object.optJSONArray("leaderboards");
        int length = array.length();
        LeaderBoard.UserLeaderBoards.UserLeaderBoard[] boards = new LeaderBoard.UserLeaderBoards.UserLeaderBoard[length];

        if (array.isEmpty())
            return null;

        JSONObject board;

        for (int index = 0; index < length; index++) {

            board = array.optJSONObject(index);
            boards[index] = new LeaderBoard.UserLeaderBoards.UserLeaderBoard(board.optString("type"), board.optString("sort"), board.optInt("place"));

        }

        return new LeaderBoard.UserLeaderBoards(user, boards);

    }

    public LeaderBoard.UserLeaderBoards getUserLeaderBoards(VimeUser user) {

        return getUserLeaderBoards(user.getId());

    }

    public Statistic getStatistic(int id) {

        String answer = getRequest("/user/" + id + "/stats");

        JSONTokener tokener = new JSONTokener(answer);
        JSONObject object = new JSONObject(tokener);

        VimeUser user = parseUser(object.optJSONObject("user"));
        JSONObject statisticObject = object.optJSONObject("stats");

        JSONArray gamesKeys = statisticObject.names();
        int length = gamesKeys.length();

        Map<String, JSONObject> games = new HashMap<>();

        for (int index = 0; index < length; index++) {

            String game = gamesKeys.optString(index);
            games.put(game, statisticObject.optJSONObject(game));

        }

        return new Statistic(user, games);

    }

    public Statistic getStatistic(VimeUser user) {

        return getStatistic(user.getId());

    }

    public Guild getGuildFromID(int id) {

        String answer = getRequest("/guild/get?id=" + id);

        JSONTokener tokener = new JSONTokener(answer);

        return parseGuild(new JSONObject(tokener));

    }

    public Guild getGuildFromTag(String tag) {

        String answer = getRequest("/guild/get?tag=" + tag);

        JSONTokener tokener = new JSONTokener(answer);

        return parseGuild(new JSONObject(tokener));

    }

    public Guild getGuildFromName(String name) {

        String answer = getRequest("/guild/get?name=" + name);

        JSONTokener tokener = new JSONTokener(answer);

        return parseGuild(new JSONObject(tokener));

    }

    public Guild[] searchGuilds(String nameOrTag) {

        String answer = getRequest("/guild/search?query=" + nameOrTag);

        JSONTokener tokener = new JSONTokener(answer);
        JSONArray array = new JSONArray(tokener);
        int length = array.length();
        Guild[] guilds = new Guild[length];

        if (array.isEmpty())
            return guilds;

        for (int index = 0; index < length; index++) {

            JSONObject object = array.optJSONObject(index);

            guilds[index] = new Guild(object.optInt("id"), object.optString("name"), object.optString("tag"), object.optString("color"), object.optInt("level"), object.optFloat("levelPercentage"), object.optString("avatar_url"));

        }

        return guilds;

    }

    public LeaderBoard getLeaderBoard(String type) {

        String answer = getRequest("/leaderboard/get/" + type);

        JSONTokener tokener = new JSONTokener(answer);
        JSONObject object = new JSONObject(tokener);

        JSONArray records = object.optJSONArray("records");
        int length = records.length();
        VimeUser[] users = new VimeUser[length];

        for (int index = 0; index < length; index++) {

            users[index] = parseUser(records.optJSONObject(index));

        }

        JSONObject board = object.optJSONObject("leaderboard");

        return new LeaderBoard(board.optString("type"), board.optString("sort"), board.optInt("offset"), board.optInt("max_size"), users);

    }

    public LeaderBoard getLeaderBoard(String type, String sort) {

        String answer = getRequest("/leaderboard/get/" + type + "/" + sort);

        JSONTokener tokener = new JSONTokener(answer);
        JSONObject object = new JSONObject(tokener);

        JSONArray records = object.optJSONArray("records");
        int length = records.length();
        VimeUser[] users = new VimeUser[length];

        for (int index = 0; index < length; index++) {

            users[index] = parseUser(records.optJSONObject(index));

        }

        JSONObject board = object.optJSONObject("leaderboard");

        return new LeaderBoard(board.optString("type"), board.optString("sort"), board.optInt("offset"), board.optInt("max_size"), users);

    }

    public Online[] getOnline() {

        String answer = getRequest("/online");

        JSONTokener tokener = new JSONTokener(answer);
        JSONObject object = new JSONObject(tokener);
        JSONObject separated = object.optJSONObject("separated");

        JSONArray keys = separated.names();
        int lengthKeys = keys.length() + 1;

        Online[] online = new Online[lengthKeys];
        online[0] = new Online("total", object.optInt("total"));

        for (int index = 1; index < lengthKeys; index++) {

            String key = keys.optString(index - 1);

            online[index] = new Online(key, separated.optInt(key));

        }

        return online;

    }

    public Maps getMaps() {

        String answer = getRequest("misc/maps");

        JSONTokener tokener = new JSONTokener(answer);
        JSONObject object = new JSONObject(tokener);

        JSONArray keys = object.names();
        int length = keys.length();

        Maps.GameMaps[] games = new Maps.GameMaps[length];

        Map<String, JSONObject> list;

        for (int keyIndex = 0; keyIndex < length; keyIndex++) {

            list = new HashMap<>();

            String key = keys.optString(keyIndex);
            JSONObject gameMaps = object.optJSONObject(key);

            JSONArray maps = gameMaps.names();

            for (int mapIndex = 0, mapsLength = maps.length(); mapIndex < mapsLength; mapIndex++) {

                String mapKey = maps.optString(mapIndex);
                JSONObject mapObject = gameMaps.optJSONObject(mapKey);

                list.put(mapKey, mapObject);

            }

            games[keyIndex] = new Maps.GameMaps(key, list);

        }

        return new Maps(games);

    }

    public Stream[] getStreams() {

        String answer = getRequest("/online/streams");

        JSONTokener tokener = new JSONTokener(answer);
        JSONArray array = new JSONArray(tokener);
        int length = array.length();
        Stream[] streams = new Stream[length];
        if (array.isEmpty())
            return streams;

        for (int index = 0; index < length; index++) {

            JSONObject stream = array.optJSONObject(index);
            streams[index] = new Stream(stream.optString("title"), stream.optString("owner"), stream.optInt("viewers"), stream.optString("url"), stream.optInt("duration"), stream.optString("platform"), parseUser(stream.optJSONObject("user")));

        }

        return streams;

    }

    @Nullable
    public Matches getLatestMatches(int amount) {

        if (amount > 100)
            throw new IllegalArgumentException("Max amount = 50");
        else if (amount < 1)
            throw new IllegalArgumentException("Min amount = 1");

        String answer = getRequest("/match/latest?count=" + amount);

        JSONTokener tokener = new JSONTokener(answer);
        JSONArray array = new JSONArray(tokener);

        int length = array.length();
        if (length < 1)
            return null;

        Matches.Match[] requestedMatches = new Matches.Match[length];

        for (int index = 0; index < length; index++) {

            JSONObject match = array.optJSONObject(index);

            JSONObject map = match.optJSONObject("map");
            Matches.Match.MatchMap matchMap = null;
            if (map != null) {

                matchMap = new Matches.Match.MatchMap(map.optString("id"), map.optString("name"), map.optInt("teams"), map.optInt("playersInTeam"));

            }

            requestedMatches[index] = new Matches.Match(null, match.optLong("id"), match.optString("game"), match.optLong("date"), match.optInt("duration"), matchMap, match);

        }

        return new Matches(null, requestedMatches, 0);

    }

    public Matches getLatestMatches() {

        return getLatestMatches(20);

    }

    public LeaderBoard.List getLeaderBoardsList() {

        String answer = getRequest("/leaderboard/list");

        JSONTokener tokener = new JSONTokener(answer);
        JSONArray array = new JSONArray(tokener);
        int length = array.length();

        LeaderBoard.List.ListLeaderBoard[] list = new LeaderBoard.List.ListLeaderBoard[length];

        for (int index = 0; index < length; index++) {

            JSONObject board = array.optJSONObject(index);

            JSONArray sortsJsonArray = board.optJSONArray("sort");
            int sortsLength = sortsJsonArray.length();
            String[] sorts = new String[sortsLength];

            for (int sortsIndex = 0; sortsIndex < sortsLength; sortsIndex++) {

                sorts[sortsIndex] = sortsJsonArray.optString(sortsIndex);

            }

            list[index] = new LeaderBoard.List.ListLeaderBoard(board.optString("type"), board.optString("description"), board.optInt("max_size"), sorts);

        }

        return new LeaderBoard.List(list);

    }

    private VimeUser parseUser(JSONObject object) {

        Session session;

        if (object.has("online")) {

            JSONObject onlineObject = object.getJSONObject("online");
            session = new Session(onlineObject.optBoolean("value"), onlineObject.optString("message"), (onlineObject.has("game") ? onlineObject.optString("game") : null));

        } else {

            session = new Session(false, "Игрок оффлайн");

        }

        Guild guild = null;

        JSONObject guildObject = object.optJSONObject("guild");

        if (guildObject != null) {

            guild = new Guild(guildObject.optInt("id"), guildObject.optString("name"), guildObject.optString("tag"), guildObject.optString("color"), guildObject.optInt("level"), guildObject.optFloat("levelPercentage"), guildObject.optString("avatar_url"));

        }

        return new VimeUser(object.optString("username"), object.optInt("id"), object.optInt("level"), object.optInt("playedSeconds"), object.optFloat("levelPercentage"), VimeUser.Rank.get(object.optString("rank")), object.optLong("lastSeen"), session, guild);

    }

    private Guild parseGuild(JSONObject object) {

        JSONObject perks = object.optJSONObject("perks");
        JSONArray members = object.optJSONArray("members");

        int length = members.length();
        Guild.GuildUser[] guildUsers = new Guild.GuildUser[length];

        for (int index = 0; index < length; index++) {

            JSONObject member = members.optJSONObject(index);

            guildUsers[index] = new Guild.GuildUser(parseUser(member.optJSONObject("user")), Guild.GuildRank.get(member.optString("status")), member.optLong("joined"), member.optLong("guildCoins"), member.optLong("guildExp"));

        }

        JSONArray perksKeys = perks.names();

        length = perksKeys.length();
        Guild.Perk[] guildPerks = new Guild.Perk[length];

        for (int index = 0; index < length; index++) {

            String key = perksKeys.optString(index);
            JSONObject perk = perks.optJSONObject(key);

            guildPerks[index] = new Guild.Perk(key, perk.optString("name"), perk.optInt("level"));

        }

        return new Guild(object.optInt("id"), object.optString("name"), object.optString("tag"), object.optString("color"), object.optInt("level"), object.optFloat("levelPercentage"), object.optString("avatar_url"), object.optLong("totalExp"), object.optLong("totalCoins"), object.optLong("created"), object.optString("web_info"), guildPerks, guildUsers, false);

    }

    private String getRequest(String request) {

        if (!this.limit.remained())
            throw new APICallException(2, "API rate limit exceeded for your IP (token rate).");

        String answer = requestToString(new HttpGet(API_URL + request + (this.token == null ? "" : "?token=" + this.token)));

        if (answer.contains("error")) {

            JSONTokener tokener = new JSONTokener(answer);
            JSONObject object = new JSONObject(tokener);

            JSONObject error = object.optJSONObject("error");

            throw new APICallException(error.optInt("error_code"), error.optString("error_msg"));

        }

        return answer;

    }

    private String requestToString(HttpRequestBase request) {

        try {

            String answer = EntityUtils.toString(this.client.execute(request).getEntity());

            if (answer == null)
                throw new IllegalArgumentException("Answer is null.");

            return answer;

        } catch (Throwable throwable) {

            throwable.printStackTrace();

        }

        throw new IllegalArgumentException("Answer is null.");

    }


    @Getter
    public static class Limit {

        private int remaining;
        private int toReset;
        private long checkedTime;

        public void update(CloseableHttpResponse response) {

            Header[] headers = response.getAllHeaders();

            for (Header header : headers) {

                if (header.getName().equals("X-RateLimit-Remaining"))
                    this.remaining = Integer.parseInt(header.getValue());
                if (header.getName().equals("X-RateLimit-Reset-After"))
                    this.toReset = Integer.parseInt(header.getValue());

            }

            this.checkedTime = System.currentTimeMillis();

        }

        public boolean remained() {

            if (remaining > 0)
                return true;

            return (int) ((System.currentTimeMillis() - checkedTime) / 1000L) > toReset;

        }

    }

}
