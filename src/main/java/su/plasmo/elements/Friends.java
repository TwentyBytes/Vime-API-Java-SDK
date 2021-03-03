package su.plasmo.elements;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Friends {

    private VimeUser user;
    private VimeUser[] friends;

}
