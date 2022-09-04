package su.plasmo.throwables;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
public class APICallException extends RuntimeException {

    /**
     * ID возникшей ошибки.
     * Список всех ошибок есть в официальной документации VimeAPI.
     * https://vimeworld.github.io/api-docs/#commonerrors
     */
    int errorId;
    /**
     * Сопутствующее ошибке сообщение.
     */
    String message;

    public APICallException(int errorId, String message) {
        super(message);
        this.errorId = errorId;
        this.message = message;
    }

}
