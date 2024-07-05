package ma.org.ancfcc.pva.core.exceptions.handlers;

import lombok.Getter;

@Getter
public class ForeignKeyConstraintException extends RuntimeException {
    private final String tableName;
    private final String constraintName;
    private final String details;

    public ForeignKeyConstraintException(String message, String tableName, String constraintName,
            String details) {
        super(message);
        this.tableName = tableName;
        this.constraintName = constraintName;
        this.details = details;
    }

}
