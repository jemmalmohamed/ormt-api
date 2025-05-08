package ma.org.ormt.modules.indicateurs.indicateur.dtos.series;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChartSeriesDto {
    private String name; // e.g. "Agriculture, forêt et pêche" - the value of principal dimension
    private String category; // e.g. "Secteur d'activité" - the name of dimension this series represents
    private List<Object> labels; // e.g. ["2019", "2020"] - temporal values for x-axis
    private List<String> secondaryDimValues; // e.g. ["Urbain", "Rural", "National"] - values of secondary dimension
    private List<Number> values; // Simple series - one value per temporal point
    private List<List<Number>> secondaryValues; // Complex series - array of values per temporal point, one for each
                                                // secondary dimension value
}