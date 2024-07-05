package ma.org.ormt.core.exceptions.handlers;

public class InvalidDeserializationException extends RuntimeException {

    private final String entityName;

    public InvalidDeserializationException(String entityName) {
        super("Invalid data format for entity: " + entityName);
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }
}
