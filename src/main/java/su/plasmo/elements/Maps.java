package su.plasmo.elements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;

import java.util.Map;

@Getter
@AllArgsConstructor
public class Maps {

    private GameMaps[] games;

    @Getter
    @AllArgsConstructor
    public static class GameMaps {

        private String game;
        private Map<String, JSONObject> maps;

    }

}
