package su.plasmo.elements;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public class VimeUser {

    String username;
    int id;
    int level;
    int playedSeconds;
    float levelPercentage;
    Rank rank;
    long lastSeen;
    //будет null если вы получаете друзей какого либо игрока
    @Nullable
    Session session;
    //будет null если игрок не состоит в гильдии
    @Nullable
    Guild guild;

    @Getter
    @AllArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public enum Rank {

        PLAYER("Игрок", "", "", "&7"),
        VIP("VIP", "[V]", "#00be00", "&a"),
        PREMIUM("Premium", "[P]", "#00dada", "&b"),
        HOLY("Holy", "[H]", "#ffba2d", "&6"),
        IMMORTAL("Immortal", "[I]", "#e800d5", "&d"),
        BUILDER("Билдер","[Билдер]", "#009c00", "&2"),
        MAPLEAD("Главный билдер","[Гл. билдер]", "#009c00", "&2"),
        YOUTUBE("YouTube","[YouTube]", "#fe3f3f", "&c"),
        DEV("Разработчик","[Dev]", "#00bebe", "&b"),
        ORGANIZER("Организатор","[Организатор]", "#00bebe", "&b"),
        MODER("Модератор","[Модер]", "#1b00ff", "&9"),
        WARDEN("Проверенный модератор","[Модер]", "#1b00ff", "&9"),
        CHIEF("Главный модератор","[Гл. модер]", "#1b00ff", "&9"),
        ADMIN("Главный админ","[Гл. админ]", "#00bebe", "&b&l");

        String chatForm;
        String prefix;
        String color;
        String minecraftColor;

        public static Rank get(String name) {
            for (Rank rank : values()) {
                if (rank.name().equalsIgnoreCase(name)) {
                    return rank;
                }
            }
            return Rank.PLAYER;
        }

    }

}
