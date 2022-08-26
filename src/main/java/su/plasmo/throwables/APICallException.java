package su.plasmo.throwables;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
public class APICallException extends RuntimeException {

    int errorId;
    String message;

    public APICallException(int errorId, String message) {
        super(message);
        this.errorId = errorId;
        this.message = message;
    }

}
