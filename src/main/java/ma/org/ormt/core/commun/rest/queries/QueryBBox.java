package ma.org.ormt.core.commun.rest.queries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class QueryBBox {

    private Double maxx;
    private Double minx;
    private Double maxy;
    private Double miny;

}