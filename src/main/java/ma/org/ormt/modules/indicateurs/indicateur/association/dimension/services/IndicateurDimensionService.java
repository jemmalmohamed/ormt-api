package ma.org.ormt.modules.indicateurs.indicateur.association.dimension.services;

import java.util.List;

import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.dtos.request.IndicateurDimensionRequestDto;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.models.IndicateurDimension;

public interface IndicateurDimensionService {

        public IndicateurDimension associateDimensionToIndicateur(IndicateurDimensionRequestDto requestDto);

        public void dissociateDimensionFromIndicateur(List<Long> ids);

        public IndicateurDimension update(Long id, IndicateurDimensionRequestDto espaceRequestDto);

}
