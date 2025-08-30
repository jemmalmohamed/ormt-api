package ma.org.ormt.modules.domaines.sousdomaine.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.base.specification.SpecificationAndPageable;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.services.DomaineService;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.details.SousDomaineDetailsDto;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.details.SousDomaineDetailsDtoMapper;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.request.SousDomaineRequestDto;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.request.SousDomaineRequestDtoMapper;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.request.ReorderSousDomaineItem;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.domaines.sousdomaine.repositories.SousDomaineRepository;
import ma.org.ormt.modules.domaines.sousdomaine.services.SousDomaineService;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.detail.IndicateurDetailDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.sousdomaine.IndicateurSousDomaineDetailDto;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@Service
public class SousDomaineServiceImpl extends BaseServiceImpl<SousDomaine> implements SousDomaineService {

    @Autowired
    private SousDomaineRepository sousDomaineRepository;

    @Autowired
    private IndicateurService indicateurService;

    @Autowired
    private DomaineService domaineService;

    @Autowired
    private ObjectsValidator<SousDomaineRequestDto> validator;

    @Autowired
    private SousDomaineRequestDtoMapper sousDomaineRequestMapper;

    @Autowired
    private SousDomaineDetailsDtoMapper sousDomaineDetailsDtoMapper;

    static final String NOT_FOUND_STRING = "SousDomaine not found";
    static final String DOMAINE_NOT_FOUND = "Domaine not found";

    public SousDomaineServiceImpl(SousDomaineRepository sousDomaineRepository,
            SpecificationService specificationService) {
        super(sousDomaineRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return sousDomaineRepository.existsById(id);
    }

    @Override
    public Optional<SousDomaine> findByNom(String nom) {
        return sousDomaineRepository.findByNom(nom);
    }

    @Override
    public Page<SousDomaine> getEntityList(QueryParams requestParams) {

        SpecificationAndPageable<SousDomaine> result = getSpecificationAndPageable(requestParams, SousDomaine.class);

        return findAll(result.getSpecification(), result.getPageable());
    }

    @Override
    public Page<SousDomaine> getEntityListByDomaineId(Long domaineId, QueryParams requestParams) {

        SpecificationAndPageable<SousDomaine> result = getSpecificationAndPageable(requestParams, SousDomaine.class);

        Specification<SousDomaine> domaineSpec = (root, _, criteriaBuilder) -> {
            Predicate domainePredicate = criteriaBuilder.equal(root.get("domaine").get("id"), domaineId);
            return domainePredicate;
        };

        Specification<SousDomaine> combinedSpec = addPredicateToSpecification(result.getSpecification(), domaineSpec);

        return findAll(combinedSpec, result.getPageable());
    }

    @Override
    public SousDomaine create(Long domaineId, SousDomaineRequestDto requestDto) {
        validator.validate(requestDto);
        Domaine domaine = domaineService.findById(
                domaineId)
                .orElseThrow(() -> new EntityNotFoundException(DOMAINE_NOT_FOUND));

        SousDomaine sousDomaineToCreate = sousDomaineRequestMapper.mapToEntity(requestDto);
        sousDomaineToCreate.setDomaine(domaine);
        return sousDomaineRepository.save(sousDomaineToCreate);
    }

    @Override
    public SousDomaine update(Long id, SousDomaineRequestDto requestDto) {
        validator.validate(requestDto);
        checkPathId(id, requestDto.getId());

        SousDomaine sousDomaine = sousDomaineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

        updateFields(sousDomaine, requestDto);

        return sousDomaineRepository.save(sousDomaine);
    }

    @Transactional
    public SousDomaine associateIndicateurToSousDomaine(Long sousDomaineId, List<Long> indicateurIds) {
        SousDomaine sousDomaine = findById(sousDomaineId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

        List<Indicateur> indicateurs = indicateurIds.stream()
                .map(id -> indicateurService.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Indicateur not found with id: " + id)))
                .collect(Collectors.toList());
        for (Indicateur indicateur : indicateurs) {
            sousDomaine.getIndicateurs().add(indicateur);
            indicateur.getSousDomaines().add(sousDomaine);
        }
        // sousDomaine.getIndicateurs().addAll(indicateurs);
        return sousDomaineRepository.save(sousDomaine);
    }

    @Transactional
    public SousDomaine dissociateIndicateurFromSousDomaine(Long sousDomaineId, List<Long> indicateurIds) {
        SousDomaine sousDomaine = findById(sousDomaineId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

        // Find all indicators that need to be dissociated
        List<Indicateur> indicateursToRemove = sousDomaine.getIndicateurs()
                .stream()
                .filter(indicateur -> indicateurIds.contains(indicateur.getId()))
                .collect(Collectors.toList());

        // Update both sides of the relationship
        for (Indicateur indicateur : indicateursToRemove) {
            sousDomaine.getIndicateurs().remove(indicateur);
            indicateur.getSousDomaines().remove(sousDomaine);
        }

        return sousDomaineRepository.save(sousDomaine);
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validateSousDomaineDependencies(id);
    }

    private void updateFields(SousDomaine sousDomaine, SousDomaineRequestDto entityToUpdate) {
        sousDomaine.setNom(entityToUpdate.getNom());
        sousDomaine.setDescription(entityToUpdate.getDescription());
        sousDomaine.setActif(entityToUpdate.getActif());
        sousDomaine.setOrdre(entityToUpdate.getOrdre());
    }

    private void validateSousDomaineDependencies(Long id) {

    }

    @Transactional
    @Override
    public void reorderSousDomaines(Long domaineId, List<ReorderSousDomaineItem> items) {
        // Validate domaine exists
        domaineService.findById(domaineId).orElseThrow(() -> new EntityNotFoundException(DOMAINE_NOT_FOUND));

        // Load existing sous-domaines for domaine
        List<SousDomaine> existing = sousDomaineRepository.findByDomaineIdOrderByOrdreAsc(domaineId);
        if (existing.isEmpty()) {
            return; // nothing to reorder
        }

        Map<Long, SousDomaine> byId = existing.stream()
                .collect(java.util.stream.Collectors.toMap(SousDomaine::getId, sd -> sd));

        Set<Long> currentIds = byId.keySet();
        Set<Long> requestedIds = items.stream().map(ReorderSousDomaineItem::getSousDomaineId)
                .collect(java.util.stream.Collectors.toSet());

        if (!currentIds.equals(requestedIds)) {
            throw new IllegalArgumentException(
                    "Reorder items must include all and only current sous-domaines for this domaine");
        }

        int n = items.size();
        boolean withinRange = items.stream()
                .allMatch(i -> i.getOrdre() != null && i.getOrdre() >= 0 && i.getOrdre() < n);
        if (!withinRange) {
            throw new IllegalArgumentException("Invalid ordre values; must be within 0.." + (n - 1));
        }
        java.util.HashSet<Integer> ordreSet = new java.util.HashSet<>();
        for (var i : items) {
            if (!ordreSet.add(i.getOrdre())) {
                throw new IllegalArgumentException("Duplicate ordre values are not allowed");
            }
        }

        Map<Long, Integer> newOrdreById = items.stream()
                .collect(java.util.stream.Collectors.toMap(
                        ReorderSousDomaineItem::getSousDomaineId,
                        ReorderSousDomaineItem::getOrdre));

        boolean anyChanged = false;
        for (SousDomaine sd : existing) {
            Integer newOrdre = newOrdreById.get(sd.getId());
            if (newOrdre != null && !newOrdre.equals(sd.getOrdre())) {
                sd.setOrdre(newOrdre);
                anyChanged = true;
            }
        }

        if (anyChanged) {
            sousDomaineRepository.saveAll(existing);
        }
    }

    @Override
    public SousDomaineDetailsDto getSousDomaineWithPivotTable(Long id, String tableFormat) {
        SousDomaine sousDomaine = findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SousDomaine not found with id: " + id));

        // Map to DetailsDto first
        SousDomaineDetailsDto dto = sousDomaineDetailsDtoMapper.mapToDto(sousDomaine);

        // Now enhance each indicateur with table data
        if (tableFormat != null && !tableFormat.isEmpty() && dto.getIndicateurs() != null) {
            for (IndicateurSousDomaineDetailDto indicateurDto : dto.getIndicateurs()) {
                // Get the full indicateur with table data
                IndicateurDetailDto indicateurWithTableData = indicateurService
                        .getIndicateurWithTableData(indicateurDto.getId(), tableFormat);

                // Copy the hasDonnees and table data fields
                indicateurDto.setPivotTableData(indicateurWithTableData.getPivotTableData());
            }
        }

        return dto;
    }

    @Override
    public List<SousDomaineDetailsDto> getSousDomainesWithPivotTable(Long domaineId, QueryParams requestParams,
            String tableFormat) {
        // Get the sous domaines for the domaine
        Page<SousDomaine> sousDomainePage = getEntityListByDomaineId(domaineId, requestParams);

        // Convert each SousDomaine to SousDomaineDetailsDto with pivot table data
        return sousDomainePage.getContent().stream()
                .map(sousDomaine -> {
                    // Map to DetailsDto first
                    SousDomaineDetailsDto dto = sousDomaineDetailsDtoMapper.mapToDto(sousDomaine);

                    // Now enhance each indicateur with table data
                    if (tableFormat != null && !tableFormat.isEmpty() && dto.getIndicateurs() != null) {
                        for (IndicateurSousDomaineDetailDto indicateurDto : dto.getIndicateurs()) {
                            // Get the full indicateur with table data
                            IndicateurDetailDto indicateurWithTableData = indicateurService
                                    .getIndicateurWithTableData(indicateurDto.getId(), tableFormat);

                            // Copy the hasDonnees and table data fields
                            indicateurDto.setPivotTableData(indicateurWithTableData.getPivotTableData());

                        }
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

}