package ma.org.ancfcc.pva.core.exceptions.handlers;

public class ShapefileUploadException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ShapefileUploadException(String message) {
        super(message);
    }

    public ShapefileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
