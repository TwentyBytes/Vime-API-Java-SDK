package su.plasmo.elements;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Stream {

    private String owner;
    private String title;
    private int viewers;
    private String url;
    private long duration;
    private String platform;
    private VimeUser user;

}
