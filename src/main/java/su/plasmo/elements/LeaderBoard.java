package su.plasmo.elements;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LeaderBoard {

    private String type;
    private String sort;
    private int offset;
    private int maxSize;

    private VimeUser[] records;

    @Getter
    @AllArgsConstructor
    public static class List {

        private ListLeaderBoard[] boards;

        @Getter
        @AllArgsConstructor
        public static class ListLeaderBoard {

            private String type;
            private String description;
            private int maxSize;
            private String[] sorts;

        }

    }

    @Getter
    @AllArgsConstructor
    public static class UserLeaderBoards {

        private VimeUser user;
        private UserLeaderBoard[] leaderBoard;

        @Getter
        @AllArgsConstructor
        public static class UserLeaderBoard {

            private String type;
            private String sort;
            private int place;

        }

    }

}
