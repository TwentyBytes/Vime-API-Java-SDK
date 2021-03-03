package su.plasmo.elements;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Online {

    //game name. Can be "total"
    private String name;
    //game current online
    private int online;

}
