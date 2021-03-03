package su.plasmo.elements;

import org.jetbrains.annotations.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VimeUser {

    private String username;
    private int id, level, playedSeconds;
    private float levelPercentage;
    private Rank rank;
    private long lastSeen;
    //будет null если вы получаете друзей какого либо игрока
    @Nullable private Session session;
    //будет null если игрок не состоит в гильдии
    @Nullable private Guild guild;

    @Getter
    @AllArgsConstructor
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

        private String chatForm, prefix, color, minecraftColor;

        public static Rank get(String name) {

            for (Rank rank : values()) {

                if (rank.name().equalsIgnoreCase(name))
                    return rank;

            }

            return Rank.PLAYER;

        }

    }

}
