package ma.org.ormt.modules.indicateurs.donnee.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
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
import ma.org.ormt.modules.indicateurs.indicateur.services.IndicateurService;
import ma.org.ormt.modules.indicateurs.valeurdimension.dtos.request.ValeurDimensionRequestDto;
import ma.org.ormt.modules.indicateurs.valeurdimension.models.ValeurDimension;
import ma.org.ormt.modules.indicateurs.valeurdimension.repositories.ValeurDimensionRepository;

@Service
@Transactional
public class DonneeIndicateurServiceImpl extends BaseServiceImpl<DonneeIndicateur> implements DonneeIndicateurService {

    @Autowired
    private DonneeIndicateurRepository donneeIndicateurRepository;

    @Autowired
    private IndicateurService indicateurService;

    @Autowired
    private ValeurDimensionRepository valeurDimensionRepository;

    @Autowired
    private DonneeIndicateurDtoMapper donneeIndicateurDtoMapper;

    @Autowired
    private DimensionService dimensionService;

    @Autowired
    private ObjectsValidator<DonneeIndicateurRequestDto> validator;

    @Autowired
    private DonneeIndicateurRequestDtoMapper donneeIndicateurRequestMapper;

    static final String NOT_FOUND_STRING = "DonneeIndicateur not found";
    static final String INDICATEUR_NOT_FOUND = "Indicateur not found";

    public DonneeIndicateurServiceImpl(DonneeIndicateurRepository donneeDonneeIndicateurRepository,
            SpecificationService specificationService) {
        super(donneeDonneeIndicateurRepository, specificationService);
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

        Specification<DonneeIndicateur> indicateurSpec = (root, _, criteriaBuilder) -> {
            Predicate indicateurPredicate = criteriaBuilder.equal(root.get("indicateur").get("id"), indicateurId);
            return indicateurPredicate;
        };

        Specification<DonneeIndicateur> combinedSpec = addPredicateToSpecification(result.getSpecification(),
                indicateurSpec);

        return findAll(combinedSpec, result.getPageable());
    }

    @Override
    public DonneeIndicateur create(Long indicateurId, DonneeIndicateurRequestDto requestDto) {
        validator.validate(requestDto);
        Indicateur indicateur = indicateurService.findById(
                indicateurId)
                .orElseThrow(() -> new EntityNotFoundException(INDICATEUR_NOT_FOUND));

        DonneeIndicateur donneeDonneeIndicateurToCreate = donneeIndicateurRequestMapper.mapToEntity(requestDto);
        donneeDonneeIndicateurToCreate.setIndicateur(indicateur);
        return donneeIndicateurRepository.save(donneeDonneeIndicateurToCreate);
    }

    @Override
    @Transactional
    public List<DonneeIndicateurDto> createByList(Long idIndicateur, List<DonneeIndicateurRequestDto> requestDto) {
        try {
            List<DonneeIndicateur> createdEntities = new ArrayList<>();

            for (DonneeIndicateurRequestDto dto : requestDto) {
                validator.validate(dto);
                Indicateur indicateur = indicateurService.findById(idIndicateur)
                        .orElseThrow(() -> new EntityNotFoundException("Indicateur non trouvé"));
                DonneeIndicateur donneeIndicateurToCreate = donneeIndicateurRequestMapper.mapToEntity(dto);
                donneeIndicateurToCreate.setIndicateur(indicateur);

                // Save DonneeIndicateur entity first
                donneeIndicateurRepository.save(donneeIndicateurToCreate);

                List<ValeurDimensionRequestDto> valeurDimensions = dto.getValeurDimensions();
                if (valeurDimensions != null && !valeurDimensions.isEmpty()) {
                    List<ValeurDimension> dimensions = valeurDimensions.stream().map(v -> {
                        Dimension dimension = dimensionService.findByNom(v.getDimensionName())
                                .orElseThrow(() -> new EntityNotFoundException("Dimension non trouvée"));
                        ValeurDimension valeurDimension = new ValeurDimension();
                        valeurDimension.setDimension(dimension);
                        valeurDimension.setValeur(v.getValeur());
                        valeurDimension.setDonneeIndicateur(donneeIndicateurToCreate);
                        return valeurDimension;
                    }).collect(Collectors.toList());

                    // Save ValeurDimension entities
                    dimensions.forEach(valeurDimensionRepository::save);
                    donneeIndicateurToCreate.setValeurDimensions(dimensions);
                }

                createdEntities.add(donneeIndicateurToCreate);
            }
            return createdEntities.stream()
                    .map(donneeIndicateurDtoMapper::mapToDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new IllegalArgumentException("Erreur lors de la création de la donneeIndicateur: " + e.getMessage());
        }
    }

    @Override
    public DonneeIndicateur update(Long id, DonneeIndicateurRequestDto requestDto) {
        validator.validate(requestDto);
        DonneeIndicateur donneeDonneeIndicateurToUpdate = donneeIndicateurRequestMapper.mapToEntity(requestDto);
        checkPathId(id, donneeDonneeIndicateurToUpdate.getId());

        DonneeIndicateur donneeDonneeIndicateur = donneeIndicateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

        updateFields(donneeDonneeIndicateur, donneeDonneeIndicateurToUpdate);

        return donneeIndicateurRepository.save(donneeDonneeIndicateur);
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validateDonneeIndicateurDependencies(id);
    }

    private void updateFields(DonneeIndicateur donneeDonneeIndicateur, DonneeIndicateur entityToUpdate) {
        donneeDonneeIndicateur.setValeur(entityToUpdate.getValeur());
    }

    private void validateDonneeIndicateurDependencies(Long id) {

    }

}