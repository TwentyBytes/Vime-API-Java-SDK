package su.plasmo.elements;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Guild {

    private int id;
    private String name;
    private String tag;
    private String color;
    private int level;
    private float levelPercentage;
    private String avatarUrl;
    private long totalExp;
    private long totalCoins;
    private long created;
    private String webInfo;
    private Perk[] perks;
    private GuildUser[] members;

    //loaded all params or not
    private boolean mini;

    public Guild(int id, String name, String tag, String color, int level, float levelPercentage, String avatarUrl) {

        this.id = id;
        this.name = name;
        this.tag = tag;
        this.color = color;
        this.level = level;
        this.levelPercentage = levelPercentage;
        this.avatarUrl = avatarUrl;

        this.mini = true;

    }

    @Getter
    @AllArgsConstructor
    public static class GuildUser {

        private VimeUser user;
        private GuildRank status;
        private long joined;
        private long guildCoins;
        private long guildExp;

    }

    public enum GuildRank {

        MEMBER,
        OFFICER,
        LEADER;

        public static GuildRank get(String name) {

            for (GuildRank rank : values()) {

                if (rank.name().equalsIgnoreCase(name))
                    return rank;

            }

            return MEMBER;

        }

    }

    @Getter
    @AllArgsConstructor
    public static class Perk {

        private String name;
        private String message;
        private int level;

    }

}
