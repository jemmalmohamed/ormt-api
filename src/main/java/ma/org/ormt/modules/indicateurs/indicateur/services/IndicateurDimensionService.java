package ma.org.ormt.modules.indicateurs.indicateur.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.dimension.repositories.DimensionRepository;
import ma.org.ormt.modules.indicateurs.indicateur.association.dtos.IndicateurDimensionRequestDto;
import ma.org.ormt.modules.indicateurs.indicateur.association.repository.IndicateurDimensionRepository;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.repositories.IndicateurRepository;

@Service
@RequiredArgsConstructor
public class IndicateurDimensionService {

        private final IndicateurRepository indicateurRepository;
        private final DimensionRepository dimensionRepository;
        private final IndicateurDimensionRepository indicateurDimensionRepository;

        @Transactional
        public Indicateur associateDimensionToIndicateur(IndicateurDimensionRequestDto requestDto) {
                Indicateur indicateur = indicateurRepository.findById(
                                requestDto.getIndicateur().getId())
                                .orElseThrow(() -> new RuntimeException("Indicateur not found"));

                Dimension dimension = dimensionRepository.findById(requestDto.getDimension().getId())
                                .orElseThrow(() -> new RuntimeException("Dimension not found"));

                IndicateurDimension indicateurDimension = new IndicateurDimension();
                indicateurDimension.setIndicateur(indicateur);
                indicateurDimension.setDimension(dimension);
                indicateurDimension.setPrincipale(requestDto.getPrincipale());
                indicateurDimension.setTemporelle(requestDto.getTemporelle());

                indicateurDimensionRepository.save(indicateurDimension);

                return indicateur;
        }

        @Transactional
        public void dissociateDimensionFromIndicateur(List<Long> ids) {

                indicateurDimensionRepository.deleteAllById(ids);
        }

        @Transactional
        public boolean setPrincipal(Long indicateurId, Long dimensionId, boolean principale) {
                Indicateur indicateur = indicateurRepository.findById(indicateurId)
                                .orElseThrow(() -> new RuntimeException("Indicateur not found"));

                // IndicateurDimension association = indicateur.getDimensionAssociations()
                // .stream()
                // .filter(assoc -> assoc.getDimension().getId().equals(dimensionId))
                // .findFirst()
                // .orElseThrow(() -> new RuntimeException("Association not found"));

                // association.setPrincipale(principale);
                indicateurRepository.save(indicateur);
                return true;
        }

        @Transactional(readOnly = true)
        public boolean isAssociated(Long indicateurId, Long dimensionId) {
                Indicateur indicateur = indicateurRepository.findById(indicateurId)
                                .orElseThrow(() -> new RuntimeException("Indicateur not found"));

                return indicateur.getDimensions().stream()
                                .anyMatch(dim -> dim.getId().equals(dimensionId));
        }
}
