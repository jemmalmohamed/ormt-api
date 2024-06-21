package ma.org.ancfcc.pva.core.validators;

import java.util.Map;
import java.util.Set;

import java.util.stream.Collectors;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ma.org.ancfcc.pva.core.exceptions.handlers.ObjectsValidationException;

import org.springframework.stereotype.Component;

@Component
public class ObjectsValidator<T> {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    /**
     * Validate the object.
     *
     * @param objectToValidate the object to validate
     */
    public void validate(T objectToValidate) {

        Set<ConstraintViolation<T>> violations = validator.validate(objectToValidate);

        if (!violations.isEmpty()) {
            Map<String, Set<String>> errorMap = violations.stream()
                    .collect(Collectors.groupingBy(violation -> {
                        String propertyPath = violation.getPropertyPath().toString();
                        return propertyPath.isEmpty() ? objectToValidate.getClass().getSimpleName() : propertyPath;
                    },
                            Collectors.mapping(ConstraintViolation::getMessage, Collectors.toSet())));
            throw new ObjectsValidationException(errorMap);
        }
    }

}
