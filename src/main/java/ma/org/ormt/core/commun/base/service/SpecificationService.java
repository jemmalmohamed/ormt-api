package ma.org.ormt.core.commun.base.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import io.micrometer.common.util.StringUtils;
import ma.org.ormt.core.commun.base.specification.GenericSpecification;
import ma.org.ormt.core.commun.rest.responses.MessageResponse;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

@Service
public class SpecificationService {
    private static final int MAX_DEPTH = 2; // Adjust based on your needs

    public <T> Specification<T> createSpecificationWithDynamicGlobalFilter(List<String> filters, String globalFilter,
            Class<T> entityClass) {

        Specification<T> specification = createSpecification(filters, "and");

        if (StringUtils.isNotBlank(globalFilter)) {
            Specification<T> dynamicGlobalSpecification = createDynamicGlobalSpecification(globalFilter, entityClass);
            specification = specification == null ? dynamicGlobalSpecification
                    : specification.and(dynamicGlobalSpecification);
        }

        return specification;
    }

    public <T> Specification<T> createDynamicGlobalSpecification(String keyword, Class<T> entityClass) {
        if (StringUtils.isBlank(keyword)) {
            return null;
        }

        List<String> filters = new ArrayList<>();
        Set<Class<?>> processedClasses = new HashSet<>();
        extractFieldsForSpecification(entityClass, "", filters, keyword, 0, processedClasses);

        return createSpecification(filters, "or");
    }

    private void extractFieldsForSpecification(Class<?> currentClass, String prefix, List<String> filters,
            String keyword, int depth, Set<Class<?>> processedClasses) {
        if (depth > MAX_DEPTH || processedClasses.contains(currentClass)) {
            return;
        }

        processedClasses.add(currentClass);

        for (Field field : currentClass.getDeclaredFields()) {
            if (field.getType() == String.class || field.getType() == Long.class || field.getType() == Integer.class
                    || field.getType() == Double.class || field.getType() == Float.class
                    || field.getType() == Short.class) {
                filters.add(prefix + field.getName() + ":like:" + keyword);
            } else {
                // Check if it's an entity (this is a basic check, adjust as needed)
                if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java.lang")) {
                    extractFieldsForSpecification(field.getType(), prefix + field.getName() + ".", filters, keyword,
                            depth + 1, processedClasses);
                }
            }
        }
    }

    public <T> Specification<T> createSpecification(List<String> filters, String operator) {
        if (filters == null || filters.isEmpty()) {
            return null;
        }

        Map<String, BiFunction<Specification<T>, Specification<T>, Specification<T>>> operators = new HashMap<>();
        operators.put("and", Specification::and);
        operators.put("or", Specification::or);

        Specification<T> specification = null;

        for (String filter : filters) {
            GenericSpecification<T> genericSpecification = createGenericSpecification(filter);
            if (genericSpecification != null) {
                BiFunction<Specification<T>, Specification<T>, Specification<T>> operatorFunction = operators
                        .get(operator);
                if (operatorFunction != null) {
                    specification = specification == null ? Specification.where(genericSpecification)
                            : operatorFunction.apply(specification, genericSpecification);
                } else {
                    String message = MessageResponse.builder()
                            .title("Invalid operator: " + operator)
                            .build()
                            .format();

                    throw new IllegalArgumentException(message);
                }
            }
        }

        return specification;
    }

    private <T> GenericSpecification<T> createGenericSpecification(String filter) {
        String[] parts = filter.split(":");
        if (parts.length >= 3) {
            String field = parts[0];
            String operation = parts[1];
            String value = parts[2];
            String upperValue = parts.length == 4 ? parts[3] : null;

            return new GenericSpecification<>(field, operation, value, upperValue);
        } else {
            return null;
        }
    }
}