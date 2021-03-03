package su.plasmo.elements;

import org.jetbrains.annotations.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Session {

    private boolean online;
    private String message;
    //game can be null even when player online if his have rank moder.
    @Nullable private String game;

    public Session(boolean online, String message) {

        this.online = online;
        this.message = message;

    }

}
