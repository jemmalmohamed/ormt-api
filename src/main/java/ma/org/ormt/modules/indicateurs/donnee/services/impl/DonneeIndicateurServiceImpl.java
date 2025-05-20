package ma.org.ormt.modules.indicateurs.donnee.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.base.specification.SpecificationAndPageable;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.dimension.services.DimensionService;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDtoMapper;
import ma.org.ormt.modules.indicateurs.donnee.dtos.request.DonneeIndicateurRequestDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.request.DonneeIndicateurRequestDtoMapper;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.donnee.repositories.DonneeIndicateurRepository;
import ma.org.ormt.modules.indicateurs.donnee.services.DonneeIndicateurService;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;
import ma.org.ormt.modules.indicateurs.valeurdimension.dtos.request.ValeurDimensionRequestDto;
import ma.org.ormt.modules.indicateurs.valeurdimension.models.ValeurDimension;
import ma.org.ormt.modules.indicateurs.valeurdimension.repositories.ValeurDimensionRepository;

@Service
@Transactional
public class DonneeIndicateurServiceImpl extends BaseServiceImpl<DonneeIndicateur> implements DonneeIndicateurService {

    private static final Logger logger = LoggerFactory.getLogger(DonneeIndicateurServiceImpl.class);
    private static final String NOT_FOUND_STRING = "DonneeIndicateur not found with ID: ";
    private static final String INDICATEUR_NOT_FOUND = "Indicateur not found with ID: ";
    private static final String DIMENSION_NOT_FOUND = "Dimension not found with name: ";
    private static final String ERROR_CREATING = "Erreur lors de la création de la donneeIndicateur: ";

    private final DonneeIndicateurRepository donneeIndicateurRepository;
    private final IndicateurService indicateurService;
    private final ValeurDimensionRepository valeurDimensionRepository;
    private final DonneeIndicateurDtoMapper donneeIndicateurDtoMapper;
    private final DimensionService dimensionService;
    private final ObjectsValidator<DonneeIndicateurRequestDto> validator;
    private final DonneeIndicateurRequestDtoMapper donneeIndicateurRequestMapper;

    @Autowired
    public DonneeIndicateurServiceImpl(
            DonneeIndicateurRepository donneeIndicateurRepository,
            SpecificationService specificationService,
            IndicateurService indicateurService,
            ValeurDimensionRepository valeurDimensionRepository,
            DonneeIndicateurDtoMapper donneeIndicateurDtoMapper,
            DimensionService dimensionService,
            ObjectsValidator<DonneeIndicateurRequestDto> validator,
            DonneeIndicateurRequestDtoMapper donneeIndicateurRequestMapper) {
        super(donneeIndicateurRepository, specificationService);
        this.donneeIndicateurRepository = donneeIndicateurRepository;
        this.indicateurService = indicateurService;
        this.valeurDimensionRepository = valeurDimensionRepository;
        this.donneeIndicateurDtoMapper = donneeIndicateurDtoMapper;
        this.dimensionService = dimensionService;
        this.validator = validator;
        this.donneeIndicateurRequestMapper = donneeIndicateurRequestMapper;
    }

    @Override
    public boolean existsById(Long id) {
        return donneeIndicateurRepository.existsById(id);
    }

    @Override
    public Page<DonneeIndicateur> getEntityList(QueryParams requestParams) {
        SpecificationAndPageable<DonneeIndicateur> result = getSpecificationAndPageable(requestParams,
                DonneeIndicateur.class);
        return findAll(result.getSpecification(), result.getPageable());
    }

    @Override
    public Page<DonneeIndicateur> getEntityListByIndicateurId(Long indicateurId, QueryParams requestParams) {
        SpecificationAndPageable<DonneeIndicateur> result = getSpecificationAndPageable(requestParams,
                DonneeIndicateur.class);

        Specification<DonneeIndicateur> indicateurSpec = (root, _, criteriaBuilder) -> criteriaBuilder
                .equal(root.get("indicateur").get("id"), indicateurId);

        Specification<DonneeIndicateur> combinedSpec = addPredicateToSpecification(result.getSpecification(),
                indicateurSpec);

        return findAll(combinedSpec, result.getPageable());
    }

    public List<DonneeIndicateur> createBulk(Long indicateurId, List<DonneeIndicateurRequestDto> requestDtos) {
        return requestDtos.stream()
                .map(dto -> create(indicateurId, dto))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DonneeIndicateur create(Long indicateurId, DonneeIndicateurRequestDto requestDto) {
        try {
            validator.validate(requestDto);

            // Get indicateur
            Indicateur indicateur = getIndicateurById(indicateurId);

            // Create and save DonneeIndicateur
            DonneeIndicateur donneeIndicateur = donneeIndicateurRequestMapper.mapToEntity(requestDto);
            donneeIndicateur.setIndicateur(indicateur);
            donneeIndicateurRepository.save(donneeIndicateur);

            // Process and save ValeurDimensions
            processValeurDimensions(requestDto.getValeurDimensions(), donneeIndicateur);

            return donneeIndicateur;
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during create operation", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error during create operation", e);
            throw new IllegalArgumentException(ERROR_CREATING + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public List<DonneeIndicateurDto> createByList(Long indicateurId, List<DonneeIndicateurRequestDto> requestDtos) {
        try {
            List<DonneeIndicateur> createdEntities = new ArrayList<>();

            // Get indicateur once for all entities
            Indicateur indicateur = getIndicateurById(indicateurId);

            for (DonneeIndicateurRequestDto dto : requestDtos) {
                validator.validate(dto);

                // Create and save DonneeIndicateur
                DonneeIndicateur donneeIndicateur = donneeIndicateurRequestMapper.mapToEntity(dto);
                donneeIndicateur.setIndicateur(indicateur);
                donneeIndicateurRepository.save(donneeIndicateur);

                // Process and save ValeurDimensions
                processValeurDimensions(dto.getValeurDimensions(), donneeIndicateur);

                createdEntities.add(donneeIndicateur);
            }

            // Map to DTOs
            return createdEntities.stream()
                    .map(donneeIndicateurDtoMapper::mapToDto)
                    .collect(Collectors.toList());
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during bulk create operation", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error during bulk create operation", e);
            throw new IllegalArgumentException(ERROR_CREATING + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public DonneeIndicateur update(Long id, DonneeIndicateurRequestDto requestDto) {
        try {
            validator.validate(requestDto);

            // Check if entity exists
            DonneeIndicateur existingDonnee = donneeIndicateurRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING + id));

            // Update base fields
            existingDonnee.setValeur(requestDto.getValeur());

            // Process ValeurDimensions if present
            if (requestDto.getValeurDimensions() != null) {
                // Remove existing ValeurDimension entities
                if (!CollectionUtils.isEmpty(existingDonnee.getValeurDimensions())) {
                    existingDonnee.getValeurDimensions()
                            .forEach(vd -> valeurDimensionRepository.deleteById(vd.getId()));
                    existingDonnee.getValeurDimensions().clear();
                }

                // Add new ValeurDimension entities
                processValeurDimensions(requestDto.getValeurDimensions(), existingDonnee);
            }

            return donneeIndicateurRepository.save(existingDonnee);
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during update operation", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error during update operation: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Error updating DonneeIndicateur: " + e.getMessage(), e);
        }
    }

    @Override
    public void validateBeforeDelete(Long id) {
        // Verify the entity exists
        donneeIndicateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING + id));

        // Add any additional validation logic here
    }

    /**
     * Helper method to get an Indicateur by ID
     */
    private Indicateur getIndicateurById(Long indicateurId) {
        return indicateurService.findById(indicateurId)
                .orElseThrow(() -> new EntityNotFoundException(INDICATEUR_NOT_FOUND + indicateurId));
    }

    /**
     * Helper method to process and save ValeurDimension entities
     */
    private void processValeurDimensions(List<ValeurDimensionRequestDto> valeurDimensionsDto,
            DonneeIndicateur donneeIndicateur) {
        if (CollectionUtils.isEmpty(valeurDimensionsDto)) {
            return;
        }

        List<ValeurDimension> dimensions = valeurDimensionsDto.stream().map(vdDto -> {
            String dimensionName = vdDto.getDimension().getNom();
            Dimension dimension = dimensionService.findByNom(dimensionName)
                    .orElseThrow(() -> new EntityNotFoundException(DIMENSION_NOT_FOUND + dimensionName));

            ValeurDimension valeurDimension = new ValeurDimension();
            valeurDimension.setDimension(dimension);
            valeurDimension.setValeur(vdDto.getValeur().toLowerCase());
            valeurDimension.setDonneeIndicateur(donneeIndicateur);
            return valeurDimension;
        }).collect(Collectors.toList());

        // Save all entities in batch
        dimensions.forEach(valeurDimensionRepository::save);

        // Update the relationship
        donneeIndicateur.setValeurDimensions(dimensions);
    }
}