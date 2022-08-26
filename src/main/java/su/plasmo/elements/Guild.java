package su.plasmo.elements;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public class Guild {

    int id;
    String name;
    String tag;
    String color;
    int level;
    float levelPercentage;
    String avatarUrl;
    long totalExp;
    long totalCoins;
    long created;
    String webInfo;
    Perk[] perks;
    GuildUser[] members;

    //loaded all params or not
    boolean mini;

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
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    @ToString
    @EqualsAndHashCode
    public static class GuildUser {

        VimeUser user;
        GuildRank status;
        long joined;
        long guildCoins;
        long guildExp;

    }

    public enum GuildRank {

        MEMBER,
        OFFICER,
        LEADER;

        public static GuildRank get(String name) {
            for (GuildRank rank : values()) {
                if (rank.name().equalsIgnoreCase(name)) {
                    return rank;
                }
            }

            return MEMBER;
        }

    }

    @Getter
    @AllArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    @ToString
    @EqualsAndHashCode
    public static class Perk {

        String name;
        String message;
        int level;

    }

}
