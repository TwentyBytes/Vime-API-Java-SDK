package su.plasmo.elements;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Stream {

    String owner;
    String title;
    int viewers;
    String url;
    long duration;
    String platform;
    VimeUser user;

}
