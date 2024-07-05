package ma.org.ormt.core.commun.rest.queries;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QuerySpatialParams extends QueryParams {

    private Double maxx;
    private Double minx;
    private Double maxy;
    private Double miny;

    // constructor
    public QuerySpatialParams(int pageIndex,
            int pageSize,
            String sortField,
            Direction sortDirection,
            List<String> filters,
            String globalFilter,
            String format,
            Double maxx,
            Double minx,
            Double maxy,
            Double miny) {
        super(pageIndex, pageSize, sortField, sortDirection, filters, globalFilter, format);
        this.maxx = maxx;
        this.minx = minx;
        this.maxy = maxy;
        this.miny = miny;
    }

    @Builder(builderMethodName = "spatialBuilder")
    public QuerySpatialParams(
            int pageIndex,
            int pageSize,
            String sortField,
            Direction sortDirection,
            int totalPages,
            long totalElements,
            String globalFilter,
            List<String> filters,
            String format,
            Double maxx,
            Double minx,
            Double maxy,
            Double miny) {
        super(pageIndex, pageSize, sortField, sortDirection, totalPages, totalElements, globalFilter, filters, format);
        this.maxx = maxx;
        this.minx = minx;
        this.maxy = maxy;
        this.miny = miny;
    }

    public static <T> QuerySpatialParams buildQuerySpatialParams(QuerySpatialParams requestParams, Page<T> entityPage) {
        return QuerySpatialParams.spatialBuilder()
                .pageIndex(entityPage.getNumber())
                .pageSize(entityPage.getSize())
                .totalPages(entityPage.getTotalPages())
                .totalElements(entityPage.getTotalElements())
                .sortDirection(requestParams.getSortDirection())
                .sortField(requestParams.getSortField())
                .filters(requestParams.getFilters())
                .globalFilter(requestParams.getGlobalFilter())
                .format(requestParams.getFormat())
                .maxx(requestParams.getMaxx())
                .minx(requestParams.getMinx())
                .maxy(requestParams.getMaxy())
                .miny(requestParams.getMiny())
                .build();
    }
}