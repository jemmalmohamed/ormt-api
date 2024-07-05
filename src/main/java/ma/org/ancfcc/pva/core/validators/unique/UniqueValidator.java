package ma.org.ancfcc.pva.core.validators.unique;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.Setter;
import ma.org.ancfcc.pva.config.ApplicationContextHolder;

@Setter
@Component
@DependsOn("applicationContextHolder")
public class UniqueValidator implements ConstraintValidator<Unique, Object> {

    String tableName;
    String fieldName;
    String fieldId;

    Class<? extends Payload>[] payload;

    @Autowired
    private EntityManager entityManager;

    public UniqueValidator() {
        ApplicationContextHolder.autowireBean(this);
    }

    @Override
    public void initialize(Unique constraintAnnotation) {
        this.tableName = constraintAnnotation.tableName();
        this.fieldName = constraintAnnotation.fieldName();
        this.fieldId = constraintAnnotation.fieldId();

    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {

        if (object == null) {
            return true;
        }

        Object fieldNameValue = new BeanWrapperImpl(object).getPropertyValue(fieldName);
        Object id = new BeanWrapperImpl(object).getPropertyValue(fieldId);

        String queryString;
        Query query;

        if (id != null) {
            // If id is not null, include it in the query to exclude the current record
            queryString = String.format("SELECT 1 FROM %s WHERE    %s = :value AND  %s NOT IN (:id)",
                    tableName,
                    fieldName,
                    fieldId);
            query = entityManager.createNativeQuery(queryString);
            query.setParameter("value", fieldNameValue);
            query.setParameter("id", id);
        } else {
            // If id is null, perform the regular unique constraint validation
            queryString = String.format("SELECT 1 FROM %s WHERE %s = :value",
                    tableName,
                    fieldName);
            query = entityManager.createNativeQuery(queryString);
            query.setParameter("value", fieldNameValue);
        }
        boolean isValid = query.getResultList().isEmpty();
        if (!isValid) {
            Object fieldValue = new BeanWrapperImpl(object).getPropertyValue(fieldName);
            if (fieldValue != null) {

                String errorMessage = context.getDefaultConstraintMessageTemplate()
                        .replace("${validatedValue." + fieldName + "}", fieldValue.toString());

                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(errorMessage)
                        .addPropertyNode(fieldName)
                        .addConstraintViolation();
            }
        }
        return isValid;
    }

}