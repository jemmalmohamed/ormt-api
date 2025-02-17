package ma.org.ormt.modules.indicateurs.dimension.dtos.association;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndicateurDimensionDto {
    private Long idIndicateur;
    private Long idDimension;
}