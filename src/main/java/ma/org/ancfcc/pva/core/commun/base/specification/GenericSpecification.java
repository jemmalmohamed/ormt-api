package ma.org.ancfcc.pva.core.commun.base.specification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import ma.org.ancfcc.pva.core.commun.rest.responses.MessageResponse;

@AllArgsConstructor
public class GenericSpecification<T> implements Specification<T> {

    private String key;
    private String operation;
    private Object value;
    private Object upperValue;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<String> keys = Arrays.asList(key.split("\\."));
        Path<?> path = root;

        for (String k : keys) {
            path = path.get(k);
        }
        if (isFieldOfTypeDate(path)) {
            return createDatePredicate(path, query, criteriaBuilder);
        } else {
            return createStandardPredicate(path, query, criteriaBuilder);
        }
    }

    private Predicate createStandardPredicate(Path<?> path, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        switch (operation) {
            case "=":
                if (value instanceof String) {
                    return criteriaBuilder.equal(criteriaBuilder.lower(path.as(String.class)),
                            ((String) value).toLowerCase());
                } else {
                    return criteriaBuilder.equal(path, value);
                }

            case ">":
                if (value instanceof Number) {
                    return criteriaBuilder.gt(path.as(Number.class), (Number) value);
                } else if (value instanceof String) {
                    return criteriaBuilder.greaterThan(path.as(String.class), (String) value);
                }
                break;
            case "<":
                if (value instanceof Number) {
                    return criteriaBuilder.lt(path.as(Number.class), (Number) value);
                } else if (value instanceof String) {
                    return criteriaBuilder.lessThan(path.as(String.class), (String) value);
                }
                break;
            case ">=":
                if (value instanceof Number) {
                    return criteriaBuilder.ge(path.as(Number.class), (Number) value);
                } else if (value instanceof String) {
                    return criteriaBuilder.greaterThanOrEqualTo(path.as(String.class), (String) value);
                }
                break;
            case "<=":
                if (value instanceof Number) {
                    return criteriaBuilder.le(path.as(Number.class), (Number) value);
                } else if (value instanceof String) {
                    return criteriaBuilder.lessThanOrEqualTo(path.as(String.class), (String) value);
                }
                break;
            case "like":

                return criteriaBuilder.like(criteriaBuilder.lower(path.as(String.class)),
                        "%" + value.toString().toLowerCase() + "%");
            case "between":
                Date lowerDate = parseDate(value.toString());
                Date upperDate = parseDate(upperValue.toString());
                return criteriaBuilder.between(path.<Date>as(Date.class), lowerDate, upperDate);
            default:
                String message = MessageResponse.builder()
                        .title("Invalid operator: " + operation)
                        .build()
                        .format();

                throw new IllegalArgumentException(message);
        }
        return null;
    }

    private Predicate createDatePredicate(Path<?> path, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        switch (operation) {

            case "=":
                return criteriaBuilder.equal(path.get(key), parseDate(value.toString()));
            case ">":
                return criteriaBuilder.greaterThan(path.<Date>get(key), parseDate(value.toString()));
            case "<":
                return criteriaBuilder.lessThan(path.<Date>get(key), parseDate(value.toString()));
            case ">=":
                return criteriaBuilder.greaterThanOrEqualTo(path.<Date>get(key), parseDate(value.toString()));
            case "<=":
                return criteriaBuilder.lessThanOrEqualTo(path.<Date>get(key), parseDate(value.toString()));
            default:
                return null;
        }
    }

    private boolean isFieldOfTypeDate(Path<?> path) {
        Class<?> type = path.getJavaType();
        return type.equals(LocalDateTime.class); // Or LocalDate.class, LocalDateTime.class,
                                                 // etc. based on your date type
    }

    // Method to parse string into Date
    private Date parseDate(String dateStr) {
        try {
            // Adjust the format as necessary
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return formatter.parse(dateStr);
        } catch (ParseException e) {

            String message = MessageResponse.builder()
                    .title("Erreur de format de date")
                    .mainMessage("Le format de date doit être yyyy-MM-dd'T'HH:mm:ss")
                    .build()
                    .format();

            throw new RuntimeException(message);
        }
    }
}
