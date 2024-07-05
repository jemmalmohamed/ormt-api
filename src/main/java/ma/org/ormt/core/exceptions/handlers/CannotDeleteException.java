package ma.org.ormt.core.exceptions.handlers;

import lombok.Getter;

@Getter
public class CannotDeleteException extends RuntimeException {

    public CannotDeleteException(String message) {
        super(message);

    }

}
