package ma.org.ormt.modules.dashboard.domaine.tbdomaine.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.utilities.EntityInspector;
import ma.org.ormt.core.utilities.PaginationUtils;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.request.TBDomaineRequestDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.request.TBDomaineRequestDtoMapper;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.details.TBDomaineDetailDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.details.TBDomaineDetailDtoMapper;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.repositories.TBDomaineRepository;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.services.TBDomaineService;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos.TBDomaineIndicateurDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.detail.IndicateurDetailDto;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@Service
public class TBDomaineServiceImpl extends BaseServiceImpl<TBDomaine> implements TBDomaineService {

    @Autowired
    private TBDomaineRepository domaineRepository;

    @Autowired
    private ObjectsValidator<TBDomaineRequestDto> validator;

    @Autowired
    private TBDomaineRequestDtoMapper domaineRequestMapper;

    @Autowired
    private TBDomaineDetailDtoMapper tbDomaineDetailMapper;

    @Autowired
    private IndicateurService indicateurService;

    static final String NOT_FOUND_STRING = "TBDomaine not found";

    public TBDomaineServiceImpl(TBDomaineRepository domaineRepository, SpecificationService specificationService) {
        super(domaineRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return domaineRepository.existsById(id);
    }

    @Override
    public Optional<TBDomaine> findByNom(String nom) {
        return domaineRepository.findByNom(nom);
    }

    @Override
    public Page<TBDomaine> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), TBDomaine.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<TBDomaine> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), TBDomaine.class);
        return findAll(specification, pageable);
    }

    @Override
    public Page<TBDomaine> getEntitiesByIds(List<Long> ids, QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);

        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), TBDomaine.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }

        // If no IDs are provided or empty list, return empty page
        if (ids == null || ids.isEmpty()) {
            return Page.empty(pageable);
        }
        // Create specification for filtering by IDs
        Specification<TBDomaine> idSpecification = (root, _, _) -> root.get("id").in(ids);

        // Get filter specification and handle null case
        Specification<TBDomaine> filterSpecification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), TBDomaine.class);

        // Combine specifications, handling null case
        Specification<TBDomaine> specification = filterSpecification != null
                ? filterSpecification.and(idSpecification)
                : idSpecification;

        return findAll(specification, pageable);
    }

    @Override
    public TBDomaine create(TBDomaineRequestDto requestDto) throws Exception {

        validator.validate(requestDto);

        TBDomaine domaineToCreate = domaineRequestMapper.mapToEntity(requestDto);
        return domaineRepository.save(domaineToCreate);

    }

    @Override
    public TBDomaine update(Long id, TBDomaineRequestDto requestDto) throws Exception {
        validator.validate(requestDto);
        checkPathId(id, requestDto.getId());
        TBDomaine tbDomaine = domaineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateTBDomaineFields(tbDomaine, requestDto);
        return domaineRepository.save(tbDomaine);
    }

    @Override
    public void addCategorie(Long tbDomaineId, Long categorieId) {
        TBDomaine tbDomaine = domaineRepository.findById(
                tbDomaineId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

        domaineRepository.save(tbDomaine);
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validateDomaineDependencies(id);
    }

    private void updateTBDomaineFields(TBDomaine tbDomaine, TBDomaineRequestDto entityToUpdate) {
        tbDomaine.setNom(entityToUpdate.getNom());
        tbDomaine.setLibelle(entityToUpdate.getLibelle());
        tbDomaine.setDescription(entityToUpdate.getDescription());
        tbDomaine.setActif(entityToUpdate.getActif());

    }

    private void validateDomaineDependencies(Long id) {
        // Check if tbDomaine exists
        // domaineRepository.findById(id)
        // .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

        // if (!tbDomaine.getSousDomaines().isEmpty()) {

        // String message = MessageResponse.builder()
        // .title("Suppression impossible ")
        // .mainMessage("Impossible de supprimer le tbdomaine car il est associé aux
        // domaines.")

        // .build()
        // .format();

        // throw new DependencyException(message);

        // }
    }

    @Override
    public boolean existsInTableauBord(Long domaineId, Long tableauBordId) {
        return domaineRepository.existsByIdAndTableaubordDomainesTableaubordId(domaineId, tableauBordId);

    }

    @Override
    public List<Long> getTBDomaineIdsByTableauBordId(Long tableauBordId) {
        return domaineRepository.findIdsByTableaubordId(tableauBordId);
    }

    @Override
    public TBDomaineDetailDto getTBDomaineWithPivotTable(Long id, String tableFormat) {
        TBDomaine tbDomaine = findById(id).orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

        // Map entity to detail DTO
        TBDomaineDetailDto dto = tbDomaineDetailMapper.mapToDto(tbDomaine);

        // Enrich each associated indicateur with pivot table data
        if (tableFormat != null && !tableFormat.isEmpty() && dto.getTbDomaineIndicateurs() != null) {
            for (TBDomaineIndicateurDto assocDto : dto.getTbDomaineIndicateurs()) {
                if (assocDto.getIndicateur() != null) {
                    IndicateurDetailDto indic = indicateurService
                            .getIndicateurWithTableData(assocDto.getIndicateur().getId(), tableFormat);
                    assocDto.getIndicateur().setPivotTableData(indic.getPivotTableData());
                }
            }
        }

        return dto;
    }

    @Override
    public List<TBDomaineDetailDto> getTBDomainesWithPivotTable(QueryParams requestParams, String tableFormat) {
        Page<TBDomaine> page = getEntityList(requestParams);

        return page.getContent().stream().map(tbDomaine -> {
            TBDomaineDetailDto dto = tbDomaineDetailMapper.mapToDto(tbDomaine);
            if (tableFormat != null && !tableFormat.isEmpty() && dto.getTbDomaineIndicateurs() != null) {
                for (TBDomaineIndicateurDto assocDto : dto.getTbDomaineIndicateurs()) {
                    if (assocDto.getIndicateur() != null) {
                        IndicateurDetailDto indic = indicateurService
                                .getIndicateurWithTableData(assocDto.getIndicateur().getId(), tableFormat);
                        assocDto.getIndicateur().setPivotTableData(indic.getPivotTableData());
                    }
                }
            }
            return dto;
        }).collect(Collectors.toList());
    }

}