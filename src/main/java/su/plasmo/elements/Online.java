package su.plasmo.elements;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public class Online {

    //game name. Can be "total"
    String name;
    //game current online
    int online;

}
