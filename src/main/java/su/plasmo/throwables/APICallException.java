package su.plasmo.throwables;

import lombok.Getter;

@Getter
public class APICallException extends RuntimeException {

    private int errorId;
    private String message;

    public APICallException(int errorId, String message) {

        super(message);

        this.errorId = errorId;
        this.message = message;

    }

}
