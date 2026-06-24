package ma.org.ormt.modules.indicateurs.indicateur.association.dimension.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.responses.MessageResponse;
import ma.org.ormt.core.exceptions.handlers.DependencyException;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.dimension.repositories.DimensionRepository;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.dtos.request.IndicateurDimensionRequestDto;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.repository.IndicateurDimensionRepository;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.repositories.IndicateurRepository;
import ma.org.ormt.modules.indicateurs.valeurdimension.repositories.ValeurDimensionRepository;

@Service
@Transactional
public class IndicateurDimensionServiceImpl extends BaseServiceImpl<IndicateurDimension>
                implements IndicateurDimensionService {

        @Autowired
        private IndicateurRepository indicateurRepository;

        @Autowired
        private DimensionRepository dimensionRepository;
        @Autowired
        private IndicateurDimensionRepository indicateurDimensionRepository;
        @Autowired
        private ValeurDimensionRepository valeurDimensionRepository;
        @Autowired
        private ObjectsValidator<IndicateurDimensionRequestDto> validator;
        private static final String NOT_FOUND_STRING = "Indicateur non trouvé";

        public IndicateurDimensionServiceImpl(
                        IndicateurDimensionRepository indicateurDimensionRepository,
                        SpecificationService specificationService) {
                super(indicateurDimensionRepository, specificationService);
        }

        @Override
        public IndicateurDimension update(Long id, IndicateurDimensionRequestDto requestDto) {
                validator.validate(requestDto);
                checkPathId(id, requestDto.getId());

                IndicateurDimension indicateurDimension = indicateurDimensionRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

                validateDimensionTypeConstraints(
                                indicateurDimension.getIndicateur().getId(),
                                requestDto.getPrincipale(),
                                requestDto.getTemporelle(),
                                id);

                indicateurDimension.setPrincipale(requestDto.getPrincipale());
                indicateurDimension.setTemporelle(requestDto.getTemporelle());

                return indicateurDimensionRepository.save(indicateurDimension);
        }

        @Override
        @Transactional
        public IndicateurDimension associateDimensionToIndicateur(IndicateurDimensionRequestDto requestDto) {
                validator.validate(requestDto);

                Indicateur indicateur = indicateurRepository.findById(
                                requestDto.getIndicateur().getId())
                                .orElseThrow(() -> new EntityNotFoundException("Indicateur non trouvé"));

                Dimension dimension = dimensionRepository.findById(requestDto.getDimension().getId())
                                .orElseThrow(() -> new EntityNotFoundException("Dimension non trouvée"));

                if (indicateurDimensionRepository.existsByIndicateurIdAndDimensionId(indicateur.getId(),
                                dimension.getId())) {
                        throw new IllegalArgumentException(buildMessage(
                                        "Association déjà existante",
                                        "Cette dimension est déjà liée à cet indicateur."));
                }

                validateDimensionTypeConstraints(
                                indicateur.getId(),
                                requestDto.getPrincipale(),
                                requestDto.getTemporelle(),
                                null);

                IndicateurDimension indicateurDimension = new IndicateurDimension();
                indicateurDimension.setIndicateur(indicateur);
                indicateurDimension.setDimension(dimension);
                indicateurDimension.setPrincipale(requestDto.getPrincipale());
                indicateurDimension.setTemporelle(requestDto.getTemporelle());

                return indicateurDimensionRepository.save(indicateurDimension);
        }

        @Transactional
        @Override
        public void dissociateDimensionFromIndicateur(List<Long> ids) {
                validateBeforeDelete(ids);
                indicateurDimensionRepository.deleteAllById(ids);
        }

        @Transactional
        public boolean setPrincipal(Long indicateurId, Long dimensionId, boolean principale) {
                Indicateur indicateur = indicateurRepository.findById(indicateurId)
                                .orElseThrow(() -> new RuntimeException("Indicateur not found"));

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

        private void validateBeforeDelete(List<Long> ids) {
                ids.forEach(id -> {
                        IndicateurDimension indicateurDimension = indicateurDimensionRepository.findById(id)
                                        .orElseThrow(() -> new RuntimeException(
                                                        "IndicateurDimension not found with id: " + id));

                        // Check if the specific combination of dimension and indicateur is used in
                        // actual data
                        Long dimensionId = indicateurDimension.getDimension().getId();
                        Long indicateurId = indicateurDimension.getIndicateur().getId();

                        if (valeurDimensionRepository.existsByDimensionAndIndicateurWithData(dimensionId,
                                        indicateurId)) {
                                String dimensionName = indicateurDimension.getDimension().getNom();
                                String indicateurName = indicateurDimension.getIndicateur().getNom();

                                String message = MessageResponse.builder()
                                                .title("Suppression impossible")
                                                .mainMessage("Impossible de supprimer l'association entre l'indicateur '"
                                                                + indicateurName + "' et la dimension '" + dimensionName
                                                                + "' car cette combinaison contient des données.")
                                                .build()
                                                .format();

                                throw new DependencyException(message);
                        }
                });
        }

        private void validateDimensionTypeConstraints(Long indicateurId, Boolean principale, Boolean temporelle,
                        Long excludeAssociationId) {
                if (Boolean.TRUE.equals(principale)
                                && indicateurDimensionRepository.existsPrincipaleForIndicateur(indicateurId,
                                                excludeAssociationId)) {
                        throw new IllegalArgumentException(buildMessage(
                                        "Dimension principale déjà définie",
                                        "Cet indicateur possède déjà une dimension principale. Veuillez modifier l'association existante."));
                }

                if (Boolean.TRUE.equals(temporelle)
                                && indicateurDimensionRepository.existsTemporelleForIndicateur(indicateurId,
                                                excludeAssociationId)) {
                        throw new IllegalArgumentException(buildMessage(
                                        "Dimension temporelle déjà définie",
                                        "Cet indicateur possède déjà une dimension temporelle. Veuillez modifier l'association existante."));
                }
        }

        private String buildMessage(String title, String mainMessage) {
                return MessageResponse.builder()
                                .title(title)
                                .mainMessage(mainMessage)
                                .build()
                                .format();
        }
}
