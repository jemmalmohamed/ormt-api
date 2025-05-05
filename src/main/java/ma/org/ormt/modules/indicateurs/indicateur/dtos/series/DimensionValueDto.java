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
public class DimensionValueDto {

    private String dimensionName; // Name of the dimension (e.g., "Region", "Année")
    private Boolean isPrincipal; // Whether this is the principal dimension
    private Boolean isTemporal; // Whether this is the temporal dimension
    private List<String> availableValues; // Available values for this dimension
    private List<String> selectedValues; // Currently selected values (for filtering)
}