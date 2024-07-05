package ma.org.ormt.core.exceptions.handlers;

public class XMLfileProcessingException extends RuntimeException {

    public XMLfileProcessingException(String message) {
        super(message);
    }

    public XMLfileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
