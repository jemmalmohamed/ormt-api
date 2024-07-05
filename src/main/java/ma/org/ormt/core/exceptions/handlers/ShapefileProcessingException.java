package ma.org.ormt.core.exceptions.handlers;

public class ShapefileProcessingException extends RuntimeException {

    public ShapefileProcessingException(String message) {
        super(message);
    }

    public ShapefileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
