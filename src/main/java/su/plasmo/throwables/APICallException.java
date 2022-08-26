package su.plasmo.throwables;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class APICallException extends RuntimeException {

    int errorId;
    String message;

    public APICallException(int errorId, String message) {
        super(message);
        this.errorId = errorId;
        this.message = message;
    }

}
