package su.plasmo.elements;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LeaderBoard {

    String type;
    String sort;
    int offset;
    int maxSize;

    VimeUser[] records;

    @Getter
    @AllArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class List {

        ListLeaderBoard[] boards;

        @Getter
        @AllArgsConstructor
        @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
        public static class ListLeaderBoard {

            String type;
            String description;
            int maxSize;
            String[] sorts;

        }

    }

    @Getter
    @AllArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class UserLeaderBoards {

        VimeUser user;
        UserLeaderBoard[] leaderBoard;

        @Getter
        @AllArgsConstructor
        @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
        public static class UserLeaderBoard {

            String type;
            String sort;
            int place;

        }

    }

}
