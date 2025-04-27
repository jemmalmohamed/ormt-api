package ma.org.ormt.core.validators.file;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = FileSizeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface FileSize {
    String message() default "La taille du fichier doit être inférieure à {max} octets";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    long max() default Long.MAX_VALUE;
}
