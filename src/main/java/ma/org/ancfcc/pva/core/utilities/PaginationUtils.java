package ma.org.ancfcc.pva.core.utilities;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ma.org.ancfcc.pva.core.commun.rest.queries.QueryParams;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationUtils {

    public static Pageable createPageable(QueryParams requestParams) {
        return PageRequest.of(
                requestParams.getPageIndex(),
                requestParams.getPageSize(),
                requestParams.getSortDirection(),
                requestParams.getSortField());
    }

}
