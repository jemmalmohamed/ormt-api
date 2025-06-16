package ma.org.ormt.modules.chiffres.services.impl;

import java.util.List;
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
import ma.org.ormt.core.utilities.EntityInspector;
import ma.org.ormt.core.utilities.PaginationUtils;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.services.DomaineService;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.donnee.services.DonneeIndicateurService;
import ma.org.ormt.modules.chiffres.association.domaine.ChiffreCleDomaine;
import ma.org.ormt.modules.chiffres.association.domaine.repository.ChiffreCleDomaineRepository;
import ma.org.ormt.modules.chiffres.dtos.request.ChiffreCleRequestDto;
import ma.org.ormt.modules.chiffres.dtos.request.ChiffreCleRequestDtoMapper;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.chiffres.repositories.ChiffreCleRepository;
import ma.org.ormt.modules.chiffres.services.ChiffreCleService;

@Service
@Transactional
public class ChiffreCleServiceImpl extends BaseServiceImpl<ChiffreCle> implements ChiffreCleService {

    @Autowired
    private ChiffreCleDomaineRepository chiffrecleDomaineRepository;
    @Autowired
    private ChiffreCleRepository chiffrecleRepository;

    @Autowired
    private DonneeIndicateurService donneeIndicateurService;

    @Autowired
    private DomaineService domaineService;

    @Autowired
    private ObjectsValidator<ChiffreCleRequestDto> validator;

    @Autowired
    private ChiffreCleRequestDtoMapper chiffrecleRequestMapper;

    private static final String NOT_FOUND_STRING = "ChiffreCle non trouvée";

    public ChiffreCleServiceImpl(ChiffreCleRepository chiffrecleRepository, SpecificationService specificationService) {
        super(chiffrecleRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return chiffrecleRepository.existsById(id);
    }

    @Override
    public Optional<ChiffreCle> findByLibelle(String libelle) {
        return chiffrecleRepository.findByLibelle(libelle);
    }

    @Override
    public Page<ChiffreCle> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), ChiffreCle.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<ChiffreCle> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), ChiffreCle.class);
        return findAll(specification, pageable);
    }

    @Override
    public Page<ChiffreCle> getEntitiesByIds(List<Long> ids, QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);

        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), ChiffreCle.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }

        // If no IDs are provided or empty list, return empty page
        if (ids == null || ids.isEmpty()) {
            return Page.empty(pageable);
        }
        // Create specification for filtering by IDs
        Specification<ChiffreCle> idSpecification = (root, _, _) -> root.get("id").in(ids);

        // Get filter specification and handle null case
        Specification<ChiffreCle> filterSpecification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), ChiffreCle.class);

        // Combine specifications, handling null case
        Specification<ChiffreCle> specification = filterSpecification != null
                ? filterSpecification.and(idSpecification)
                : idSpecification;

        return findAll(specification, pageable);
    }

    @Override
    public ChiffreCle save(ChiffreCle chiffrecle) {
        return chiffrecleRepository.save(chiffrecle);
    }

    @Override
    public ChiffreCle create(ChiffreCleRequestDto requestDto) throws Exception {

        validator.validate(requestDto);

        ChiffreCle chiffrecleToCreate = chiffrecleRequestMapper.mapToEntity(requestDto);

        if (requestDto.getDonneeIndicateurId() != null) {
            DonneeIndicateur donneeIndicateur = donneeIndicateurService.findById(requestDto.getDonneeIndicateurId())
                    .orElseThrow(() -> new EntityNotFoundException("Donnee Indicateur non trouvé"));
            chiffrecleToCreate.setDonneeIndicateur(donneeIndicateur);
        }
        ChiffreCle createdChiffreCle = chiffrecleRepository.save(chiffrecleToCreate);
        return createdChiffreCle;

    }

    @Override
    public ChiffreCle update(Long id, ChiffreCleRequestDto requestDto) throws Exception {
        validator.validate(requestDto);
        checkPathId(id, requestDto.getId());
        ChiffreCle chiffrecle = chiffrecleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateChiffreCleFields(chiffrecle, requestDto);

        if (requestDto.getDonneeIndicateurId() != null) {
            DonneeIndicateur donneeIndicateur = donneeIndicateurService.findById(requestDto.getDonneeIndicateurId())
                    .orElseThrow(() -> new EntityNotFoundException("Donnee Indicateur non trouvé"));
            chiffrecle.setDonneeIndicateur(donneeIndicateur);
        } else {
            chiffrecle.setDonneeIndicateur(null);
        }

        return chiffrecleRepository.save(chiffrecle);
    }

    private void updateChiffreCleFields(ChiffreCle chiffrecle, ChiffreCleRequestDto dto) {
        chiffrecle.setLibelle(dto.getLibelle().toLowerCase());
        chiffrecle.setValeur(dto.getValeur());
        chiffrecle.setUnite(dto.getUnite().toLowerCase());
        chiffrecle.setDescription(dto.getDescription().toLowerCase());
        chiffrecle.setAccessType(dto.getAccessType());
        chiffrecle.setActif(dto.getActif());
    }

    public void attachDomaine(Long chiffrecleId, Long domaineId) {
        ChiffreCle chiffrecle = chiffrecleRepository.findById(chiffrecleId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        Domaine domaine = domaineService.findById(domaineId)
                .orElseThrow(() -> new EntityNotFoundException("Domaine non trouvé"));

        ChiffreCleDomaine chiffrecleDomaine = new ChiffreCleDomaine();
        chiffrecleDomaine.setChiffreCle(chiffrecle);
        chiffrecleDomaine.setDomaine(domaine);

        chiffrecleDomaineRepository.save(chiffrecleDomaine);
    }

    public void detachDomaine(Long chiffrecleDomaineId) {

        ChiffreCleDomaine chiffrecleDomaine = chiffrecleDomaineRepository.findById(chiffrecleDomaineId)
                .orElseThrow(() -> new EntityNotFoundException("Association non trouvée"));

        chiffrecleDomaineRepository.delete(chiffrecleDomaine);
    }

}