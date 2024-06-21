package ma.org.ancfcc.pva.core.exceptions.handlers;

import lombok.Getter;

@Getter
public class CannotDeleteException extends RuntimeException {

    public CannotDeleteException(String message) {
        super(message);

    }

}
