package su.plasmo.elements;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public class Stream {

    String owner;
    String title;
    int viewers;
    String url;
    long duration;
    String platform;
    VimeUser user;

}
