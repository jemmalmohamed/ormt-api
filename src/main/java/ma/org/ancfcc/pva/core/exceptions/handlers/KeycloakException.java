package ma.org.ancfcc.pva.core.exceptions.handlers;

import lombok.Getter;

@Getter
public class KeycloakException extends RuntimeException {

    public KeycloakException(String message) {
        super(message);

    }

}
