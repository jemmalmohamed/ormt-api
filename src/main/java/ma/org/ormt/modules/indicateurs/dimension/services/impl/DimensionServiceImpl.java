package ma.org.ormt.modules.indicateurs.dimension.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.commun.rest.responses.MessageResponse;
import ma.org.ormt.core.exceptions.handlers.DependencyException;
import ma.org.ormt.core.utilities.EntityInspector;
import ma.org.ormt.core.utilities.PaginationUtils;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.indicateurs.dimension.dtos.request.DimensionRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.dtos.request.DimensionRequestDtoMapper;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.dimension.repositories.DimensionRepository;
import ma.org.ormt.modules.indicateurs.dimension.services.DimensionService;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@Service
@Transactional
public class DimensionServiceImpl extends BaseServiceImpl<Dimension> implements DimensionService {

    @Autowired
    private DimensionRepository dimensionRepository;

    @Autowired
    private IndicateurService indicateurService;

    @Autowired
    private ObjectsValidator<DimensionRequestDto> validator;

    @Autowired
    private DimensionRequestDtoMapper dimensionRequestMapper;

    private static final String NOT_FOUND_STRING = "Dimension non trouvée";
    private static final String INDICATEUR_NOT_FOUND = "Indicateur non trouvé";

    public DimensionServiceImpl(DimensionRepository dimensionRepository, SpecificationService specificationService) {
        super(dimensionRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return dimensionRepository.existsById(id);
    }

    @Override
    public Optional<Dimension> findByNom(String nom) {
        return dimensionRepository.findByNom(nom);
    }

    @Override
    public Page<Dimension> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Dimension.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Dimension> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Dimension.class);
        return findAll(specification, pageable);
    }

    @Override
    public Dimension save(Dimension dimension) {
        return dimensionRepository.save(dimension);
    }

    @Override
    public Dimension create(DimensionRequestDto requestDto) {
        validator.validate(requestDto);
        Dimension dimensionToCreate = dimensionRequestMapper.mapToEntity(requestDto);
        return dimensionRepository.save(dimensionToCreate);
    }

    @Override
    public Dimension update(Long id, DimensionRequestDto requestDto) {
        validator.validate(requestDto);
        checkPathId(id, requestDto.getId());

        Dimension dimension = dimensionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

        updateFields(dimension, requestDto);
        return dimensionRepository.save(dimension);
    }

    @Override
    @Transactional
    public void associateWithIndicateur(Long dimensionId, Long indicateurId) {
        try {
            Dimension dimension = dimensionRepository.findById(dimensionId)
                    .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

            Indicateur indicateur = indicateurService.findById(indicateurId)
                    .orElseThrow(() -> new EntityNotFoundException(INDICATEUR_NOT_FOUND));

            if (indicateur.getDimensions().contains(dimension)) {
                throw new IllegalArgumentException("Cette dimension est déjà associée à cet indicateur");
            }

            indicateur.getDimensions().add(dimension);
            indicateurService.save(indicateur);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Erreur lors de l'association de la dimension avec l'indicateur: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void dissociateFromIndicateur(Long dimensionId, Long indicateurId) {
        try {
            Dimension dimension = dimensionRepository.findById(dimensionId)
                    .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

            Indicateur indicateur = indicateurService.findById(indicateurId)
                    .orElseThrow(() -> new EntityNotFoundException(INDICATEUR_NOT_FOUND));

            if (!indicateur.getDimensions().contains(dimension)) {
                throw new IllegalArgumentException("Cette dimension n'est pas associée à cet indicateur");
            }

            indicateur.getDimensions().remove(dimension);
            indicateurService.save(indicateur);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Erreur lors de la dissociation de la dimension de l'indicateur: " + e.getMessage());
        }
    }

    private void updateFields(Dimension dimension, DimensionRequestDto entityToUpdate) {
        dimension.setNom(entityToUpdate.getNom().toLowerCase());
        dimension.setLibelle(entityToUpdate.getLibelle().toLowerCase());
        dimension.setType(entityToUpdate.getType().toLowerCase());
        dimension.setDescription(entityToUpdate.getDescription().toLowerCase());
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validateDimensionDependencies(id);
    }

    private void validateDimensionDependencies(Long id) {
        Dimension dimension = dimensionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        if (!dimension.getIndicateurDimensions().isEmpty()) {

            String message = MessageResponse.builder()
                    .title("Suppression impossible ")
                    .mainMessage("Impossible de supprimer la dimension car elle est associée à des indicateurs.")

                    .build()
                    .format();

            throw new DependencyException(message);

        }
    }

}