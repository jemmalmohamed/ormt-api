package ma.org.ormt.core.exceptions.handlers;

import lombok.Getter;

@Getter
public class KeycloakException extends RuntimeException {

    public KeycloakException(String message) {
        super(message);

    }

}
