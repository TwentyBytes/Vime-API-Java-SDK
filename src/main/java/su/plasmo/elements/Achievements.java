package su.plasmo.elements;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public class Achievements {

    VimeUser user;
    Achievement[] achievements;

    @Getter
    @AllArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    @ToString
    @EqualsAndHashCode
    public static class Achievement {

        int id;
        long time;

    }

    @Getter
    @AllArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    @ToString
    @EqualsAndHashCode
    public static class List {

        Map<Integer, ListAchievement> achievements;

        @Getter
        @AllArgsConstructor
        @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
        @ToString
        @EqualsAndHashCode
        public static class ListAchievement {

            int id;
            String title;
            int reward;
            String[] description;

        }

        @Nullable
        public ListAchievement get(int id) {
            return achievements.get(id);
        }

    }

}
