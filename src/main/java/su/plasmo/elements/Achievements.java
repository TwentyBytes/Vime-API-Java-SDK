package su.plasmo.elements;

import org.jetbrains.annotations.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class Achievements {

    private VimeUser user;
    private Achievement[] achievements;

    @Getter
    @AllArgsConstructor
    public static class Achievement {

        private int id;
        private long time;

    }

    @Getter
    @AllArgsConstructor
    public static class List {

        private Map<Integer, ListAchievement> achievements;

        @Getter
        @AllArgsConstructor
        public static class ListAchievement {

            private int id;
            private String title;
            private int reward;
            private String[] description;

        }

        @Nullable
        public ListAchievement get(int id) {

            return achievements.get(id);

        }

    }

}
