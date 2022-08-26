package su.plasmo.elements;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;

import java.util.Map;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Statistic {

    VimeUser user;
    Map<String, JSONObject> games;

    @Nullable
    public JSONObject getGame(String name) {
        for (Map.Entry<String, JSONObject> entry : games.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

}
