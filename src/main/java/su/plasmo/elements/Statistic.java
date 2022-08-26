package su.plasmo.elements;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.Map;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
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
