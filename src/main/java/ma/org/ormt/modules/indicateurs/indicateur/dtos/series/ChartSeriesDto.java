package ma.org.ormt.modules.indicateurs.indicateur.dtos.series;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChartSeriesDto {

    private String name; // Series name (e.g., "Region Paris")
    private String category; // Category name (e.g., "Region")
    private List<Object> labels; // X-axis labels (typically time periods)
    private List<Number> values; // Y-axis values
    private String color; // Optional - for consistent coloring
    private Object metadata; // Additional metadata for the series (can be used for tooltips, etc.)
}