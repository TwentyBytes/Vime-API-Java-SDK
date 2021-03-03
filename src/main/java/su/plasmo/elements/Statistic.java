package su.plasmo.elements;

import org.jetbrains.annotations.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;

import java.util.Map;

@Getter
@AllArgsConstructor
public class Statistic {

    private VimeUser user;
    private Map<String, JSONObject> games;

    @Nullable
    public JSONObject getGame(String name) {

        for (Map.Entry<String, JSONObject> entry : games.entrySet()) {

            if (entry.getKey().equalsIgnoreCase(name))
                return entry.getValue();

        }

        return null;

    }

}
