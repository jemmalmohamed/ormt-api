package ma.org.ancfcc.pva.core.commun.rest.queries;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class QueryParams {

    protected int pageIndex;
    private int pageSize;
    private String sortField;
    private Direction sortDirection;

    private int totalPages;
    private long totalElements;

    private String globalFilter;
    private List<String> filters;

    private String format;

    // create constructor
    public QueryParams(
            int pageIndex,
            int pageSize,
            String sortField,
            Direction sortDirection,
            List<String> filters,
            String globalFilter) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.sortField = sortField;
        this.sortDirection = sortDirection;
        this.globalFilter = globalFilter;
        this.filters = filters;

    }

    public QueryParams(
            int pageIndex,
            int pageSize,
            String sortField,
            Direction sortDirection,
            List<String> filters,
            String globalFilter,
            String format) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.sortField = sortField;
        this.sortDirection = sortDirection;
        this.globalFilter = globalFilter;
        this.filters = filters;
        this.format = format;
    }

    public static <T> QueryParams buildQueryParams(QueryParams requestParams, Page<T> entityPage) {

        return QueryParams.builder()

                .pageIndex(entityPage.getNumber())
                .pageSize(entityPage.getSize())
                .totalPages(entityPage.getTotalPages())
                .totalElements(entityPage.getTotalElements())

                .sortDirection(requestParams.getSortDirection())
                .sortField(requestParams.getSortField())
                .filters(requestParams.getFilters())
                .globalFilter(requestParams.getGlobalFilter())

                .build();
    }
}