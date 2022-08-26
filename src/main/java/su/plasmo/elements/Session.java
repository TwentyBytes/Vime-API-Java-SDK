package su.plasmo.elements;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Session {

    boolean online;
    String message;
    //game can be null even when player online if his have rank moder.
    @Nullable
    String game;

    public Session(boolean online, String message) {
        this.online = online;
        this.message = message;
    }

}
