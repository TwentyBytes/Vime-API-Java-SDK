package su.plasmo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
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
import su.plasmo.logic.HTTPRequester;
import su.plasmo.logic.Limit;
import su.plasmo.throwables.APICallException;

import java.util.HashMap;
import java.util.Map;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PlasmoVimeAPI {

    String token;
    HTTPRequester requester;

    public PlasmoVimeAPI(String token) {
        this.token = token;
        this.requester = new HTTPRequester(token);
    }

    /**
     * Получение пользователя по ID.
     *
     * @param id - id пользователя.
     * @return VimeUser instance - пользователь.
     */
    public VimeUser getUser(int id) {
        String answer = requester.GET("user/" + id);

        JSONTokener tokener = new JSONTokener(answer);
        JSONArray array = new JSONArray(tokener);
        JSONObject object = array.optJSONObject(0);

        return parseUser(object);
    }


    /**
     * Получение пользователя по нику.
     *
     * @param name - ник пользователя.
     * @return VimeUser instance - пользователь.
     */
    public VimeUser getUser(String name) {
        String answer = requester.GET("user/name/" + name);

        JSONTokener tokener = new JSONTokener(answer);
        JSONArray array = new JSONArray(tokener);

        return parseUser(array.optJSONObject(0));
    }

    /**
     * Получение нескольких пользователей по id.
     *
     * @param ids - массив id получаемых пользователей.
     *            Не больше 1000 за раз
     *            и не меньше 1.
     * @return Массив VimeUser - пользователей.
     */
    @SneakyThrows
    public VimeUser[] getUsers(int... ids) {
        if (ids.length > 1000) {
            throw new IllegalArgumentException("Players amount can`t be > 1000");
        }

        VimeUser[] users = new VimeUser[ids.length];
        if (ids.length < 1) {
            return users;
        }

        JSONArray array = new JSONArray();
        for (int id : ids) {
            array.put(id);
        }

        HttpPost post = new HttpPost("https://api.vimeworld.ru/" + "user/session" + (this.token == null ? "" : "?token=" + this.token));
        post.setEntity(new StringEntity(array.toString()));

        String answer = requester.POST(post);

        JSONTokener tokener = new JSONTokener(answer);
        JSONArray userObjectsArray = new JSONArray(tokener);

        for (int index = 0, length = users.length; index < length; index++) {
            users[index] = parseUser(userObjectsArray.optJSONObject(index));
        }

        return users;
    }

    /**
     * Получение нескольких пользователей по id.
     *
     * @param names - массив ников получаемых пользователей.
     *            Не больше 50 за раз
     *            и не меньше 1.
     * @return Массив VimeUser - пользователей.
     */
    public VimeUser[] getUsers(String... names) {
        if (names.length > 50) {
            throw new IllegalArgumentException("Max users amount 50");
        } else if (names.length < 1) {
            throw new IllegalArgumentException("Min users amount 1");
        }

        StringBuilder builder = new StringBuilder();

        for (int index = 0, length = names.length; index < length; index++) {
            builder.append(names[index]);
            if (index != length - 1) {
                builder.append(",");
            }
        }

        String answer = requester.GET("user/name/" + builder.toString());

        JSONTokener tokener = new JSONTokener(answer);
        JSONArray array = new JSONArray(tokener);
        int length = array.length();
        VimeUser[] users = new VimeUser[length];

        for (int index = 0; index < length; index++) {
            users[index] = parseUser(array.optJSONObject(index));
        }

        return users;
    }

    /**
     * Получение всех стафф-юзеров (админы, модеры и др.)
     *
     * @return Массив VimeUser - пользователей.
     */
    public VimeUser[] getOnlineStaff() {
        String answer = requester.GET("online/staff");

        JSONTokener tokener = new JSONTokener(answer);
        JSONArray array = new JSONArray(tokener);
        int length = array.length();
        VimeUser[] users = new VimeUser[length];
        if (array.isEmpty()) {
            return users;
        }

        for (int index = 0; index < length; index++) {
            users[index] = parseUser(array.optJSONObject(index));
        }

        return users;
    }

    /**
     * Получение матча по ID.
     *
     * @param matchID - id матча.
     * @return Матч.
     */
    public Matches.FullMatch getMatch(int matchID) {
        String answer = requester.GET("match/" + matchID);

        JSONTokener tokener = new JSONTokener(answer);
        JSONObject object = new JSONObject(tokener);

        return new Matches.FullMatch(object.optInt("version"), object.optString("game"), object.optString("server"), object.optLong("start"), object.optLong("end"), object.optString("mapName"), object.optString("mapId"),
                object.has("owned"), object);
    }

    /**
     * Получение последних 20 матчей пользователя.
     *
     * @param id - id пользователя.
     * @return Матчи.
     */
    public Matches getUserMatches(int id) {
        return getUserMatches(id, 20, 0);
    }

    /**
     * Получение матчей пользователя.
     *
     * @param id - id пользователя.
     * @param amount - количество получаемых матчей.
     * @return Матчи.
     */
    public Matches getUserMatches(int id, int amount) {
        return getUserMatches(id, amount, 0);
    }

    /**
     * Получение матчей пользователя.
     *
     * @param id - id пользователя.
     * @param amount - количество получаемых матчей.
     * @param offset - сколько матчей нужно пропустить (от начала списка).
     *              Максимум 2000.
     * @return Матчи.
     */
    @Nullable
    public Matches getUserMatches(int id, int amount, int offset) {
        if (amount > 50) {
            throw new IllegalArgumentException("Max amount = 50");
        } else if (amount < 1) {
            throw new IllegalArgumentException("Min amount = 1");
        }

        if (offset > 2000) {
            throw new IllegalArgumentException("Max offset = 2000");
        }

        String answer = requester.GET("user/" + id + "/matches");

        JSONTokener tokener = new JSONTokener(answer);
        JSONObject object = new JSONObject(tokener);

        VimeUser user = parseUser(object.optJSONObject("user"));
        int skipped = object.optJSONObject("request").optInt("offset");

        JSONArray array = object.optJSONArray("matches");

        int length = array.length();
        if (length < 1) {
            return null;
        }

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

    /**
     * Получение достижений пользователя.
     *
     * @param id - ID пользователя.
     * @return достижения.
     */
    @Nullable
    public Achievements getAchievements(int id) {
        String answer = requester.GET("user/" + id + "/achievements");

        JSONTokener tokener = new JSONTokener(answer);
        JSONObject object = new JSONObject(tokener);

        JSONObject userObject = object.optJSONObject("user");
        VimeUser user = parseUser(userObject);

        JSONArray achievementsArray = object.optJSONArray("achievements");
        int length = achievementsArray.length();

        if (achievementsArray.isEmpty()) {
            return null;
        }

        Achievements.Achievement[] achievements = new Achievements.Achievement[length];

        for (int index = 0; index < length; index++) {
            JSONObject achievement = achievementsArray.optJSONObject(index);
            achievements[index] = new Achievements.Achievement(achievement.optInt("id"), achievement.optLong("time"));
        }

        return new Achievements(user, achievements);
    }


    /**
     * Получение достижений пользователя.
     *
     * @param user - пользователь.
     * @return Достижения пользователя.
     */
    public Achievements getAchievements(VimeUser user) {
        return getAchievements(user.getId());
    }

    public Achievements.List getAchievementsList() {
        String answer = requester.GET("misc/achievements");

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

    /**
     * Получение друзей пользователя.
     *
     * @param id - ID пользователя.
     * @return Друзья пользователя.
     */
    @Nullable
    public Friends getUserFriends(int id) {
        String answer = requester.GET("user/" + id + "/friends");

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

    /**
     * Получение друзей пользователя.
     *
     * @param user - пользователь.
     * @return Друзья пользователя.
     */
    public Friends getUserFriends(VimeUser user) {
        return getUserFriends(user.getId());
    }

    /**
     * Получение списка всех топов в которых есть указанный пользователь.
     *
     * @param id - ID пользователя.
     * @return Список топов.
     */
    @Nullable
    public LeaderBoard.UserLeaderBoards getUserLeaderBoards(int id) {
        String answer = requester.GET("user/" + id + "/leaderboards");

        JSONTokener tokener = new JSONTokener(answer);
        JSONObject object = new JSONObject(tokener);

        JSONObject userObject = object.optJSONObject("user");
        VimeUser user = parseUser(userObject);

        JSONArray array = object.optJSONArray("leaderboards");
        int length = array.length();
        LeaderBoard.UserLeaderBoards.UserLeaderBoard[] boards = new LeaderBoard.UserLeaderBoards.UserLeaderBoard[length];

        if (array.isEmpty()) {
            return null;
        }

        JSONObject board;

        for (int index = 0; index < length; index++) {
            board = array.optJSONObject(index);
            boards[index] = new LeaderBoard.UserLeaderBoards.UserLeaderBoard(board.optString("type"), board.optString("sort"), board.optInt("place"));
        }

        return new LeaderBoard.UserLeaderBoards(user, boards);
    }

    /**
     * Получение списка всех топов в которых есть указанный пользователь.
     *
     * @param user - пользователь.
     * @return Список топов.
     */
    public LeaderBoard.UserLeaderBoards getUserLeaderBoards(VimeUser user) {
        return getUserLeaderBoards(user.getId());
    }

    /**
     * Получение статистики пользователь.
     *
     * @param id - ID пользователя.
     * @return Статистика пользователя.
     */
    public Statistic getStatistic(int id) {
        String answer = requester.GET("user/" + id + "/stats");

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


    /**
     * Получение статистики пользователь.
     *
     * @param user - пользователь.
     * @return Статистика пользователя.
     */
    public Statistic getStatistic(VimeUser user) {
        return getStatistic(user.getId());
    }

    /**
     * Получение гильдии по ID.
     *
     * @param id - ID гильдии.
     * @return Гильдия.
     */
    public Guild getGuildFromID(int id) {
        String answer = requester.GET("guild/get?id=" + id);

        JSONTokener tokener = new JSONTokener(answer);

        return parseGuild(new JSONObject(tokener));
    }

    /**
     * Получение гильдии по тегу.
     *
     * @param tag - тег гильдии.
     * @return Гильдия.
     */
    public Guild getGuildFromTag(String tag) {
        String answer = requester.GET("guild/get?tag=" + tag);
        JSONTokener tokener = new JSONTokener(answer);
        return parseGuild(new JSONObject(tokener));
    }

    /**
     * Получение гильдии по названию.
     *
     * @param name - название гильдии.
     * @return Гильдия.
     */
    public Guild getGuildFromName(String name) {
        String answer = requester.GET("guild/get?name=" + name.replace(" ", "%20"));
        JSONTokener tokener = new JSONTokener(answer);
        return parseGuild(new JSONObject(tokener));
    }

    /**
     * Поиск гильдий по заданному параметру.
     * Параметр может быть как и названием искомой гильдии, так и тегом.
     *
     * @param nameOrTag - Имя или тег.
     * @return Массив найденных гильдий по заданным данным.
     */
    public Guild[] searchGuilds(String nameOrTag) {
        String answer = requester.GET("guild/search?query=" + nameOrTag.replace(" ", "%20"));

        JSONTokener tokener = new JSONTokener(answer);
        JSONArray array = new JSONArray(tokener);
        int length = array.length();
        Guild[] guilds = new Guild[length];

        if (array.isEmpty()) {
            return guilds;
        }

        for (int index = 0; index < length; index++) {
            JSONObject object = array.optJSONObject(index);
            guilds[index] = new Guild(object.optInt("id"), object.optString("name"), object.optString("tag"), object.optString("color"), object.optInt("level"), object.optFloat("levelPercentage"), object.optString("avatar_url"));
        }

        return guilds;
    }

    /**
     * Получение топа по указанному ID (типу).
     *
     * Типы всех топов можно получить с помощью метода
     * {@link #getLeaderBoardsList()}
     *
     * @param type - тип (ID)
     * @return Топ.
     */
    public LeaderBoard getLeaderBoard(String type) {
        String answer = requester.GET("leaderboard/get/" + type);

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

    /**
     * Получение топа по указанному ID (типу)
     * и по указанной сортировке.
     *
     * Типы и сортировки всех топов можно получить с помощью метода
     * {@link #getLeaderBoardsList()}
     *
     * @param type - тип (ID)
     * @param sort - сортировка.
     * @return Топ.
     */
    public LeaderBoard getLeaderBoard(String type, String sort) {
        String answer = requester.GET("leaderboard/get/" + type + "/" + sort);

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


    /**
     * Получение онлайнов на всех режимах.
     *
     * @return Массив с онлайнами на всех режимах, а так-же общий total онлайн.
     */
    public Online[] getOnline() {
        String answer = requester.GET("online");

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

    /**
     * Получение карт всех режимов.
     *
     * @return Все карты всех режимов.
     */
    public Maps getMaps() {
        String answer = requester.GET("misc/maps");

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

    /**
     * Получение всех стримов ютуберов на данный момент.
     *
     * @return Массив стримов.
     */
    public Stream[] getStreams() {
        String answer = requester.GET("online/streams");

        JSONTokener tokener = new JSONTokener(answer);
        JSONArray array = new JSONArray(tokener);
        int length = array.length();
        Stream[] streams = new Stream[length];
        if (array.isEmpty()) {
            return streams;
        }

        for (int index = 0; index < length; index++) {
            JSONObject stream = array.optJSONObject(index);
            streams[index] = new Stream(stream.optString("title"), stream.optString("owner"), stream.optInt("viewers"), stream.optString("url"), stream.optInt("duration"), stream.optString("platform"), parseUser(stream.optJSONObject("user")));
        }

        return streams;
    }

    /**
     * Получение последних сыгранных (уже завершенных не приватных) матчей.
     *
     * @param amount - количество получаемых матчей.
     * @return Матчи в указанном количестве.
     */
    @Nullable
    public Matches getLatestMatches(int amount) {
        if (amount > 100) {
            throw new IllegalArgumentException("Max amount = 50");
        }
        else if (amount < 1) {
            throw new IllegalArgumentException("Min amount = 1");
        }

        String answer = requester.GET("match/latest?count=" + amount);

        JSONTokener tokener = new JSONTokener(answer);
        JSONArray array = new JSONArray(tokener);

        int length = array.length();
        if (length < 1) {
            return null;
        }

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

    /**
     * Получение последних сыгранных (уже завершенных не приватных) матчей.
     *
     * @return 20 последних матчей.
     */
    public Matches getLatestMatches() {
        return getLatestMatches(20);
    }

    /**
     * Получение ID всех топов с описанием, максимальным размером и
     * названием сортировок.
     *
     * @return Список всех топов.
     */
    public LeaderBoard.List getLeaderBoardsList() {
        String answer = requester.GET("leaderboard/list");

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

    public static PlasmoVimeAPI construct(String token) {
        return new PlasmoVimeAPI(token);
    }

    public static PlasmoVimeAPI construct() {
        return construct(null);
    }

}
