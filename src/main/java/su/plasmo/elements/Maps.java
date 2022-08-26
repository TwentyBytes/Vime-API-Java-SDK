package su.plasmo.elements;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.json.JSONObject;

import java.util.Map;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public class Maps {

    GameMaps[] games;

    @Getter
    @AllArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    @ToString
    @EqualsAndHashCode
    public static class GameMaps {

        String game;
        Map<String, JSONObject> maps;

    }

}
