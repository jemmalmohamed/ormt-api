package ma.org.ormt.core.utilities;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ma.org.ormt.core.commun.rest.queries.QueryParams;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationUtils {
    public static Pageable createPageable(QueryParams requestParams) {
        return PageRequest.of(
                requestParams.getPageIndex(),
                requestParams.getPageSize(),
                requestParams.getSortDirection(),
                requestParams.getSortField());
    }

    public static Pageable validateAndCleanSort(Pageable pageable, Class<?> entityClass) {
        if (pageable.getSort().isSorted()) {
            boolean allValid = pageable.getSort().stream()
                    .allMatch(order -> EntityInspector.isFieldPresentInEntity(order.getProperty(), entityClass));
            if (!allValid) {
                return Pageable.ofSize(pageable.getPageSize()).withPage(pageable.getPageNumber());
            }
        }
        return pageable;
    }
}
