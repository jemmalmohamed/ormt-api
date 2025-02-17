package ma.org.ormt.modules.domaines.sousdomaine.services.impl;

import java.util.Optional;

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
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.services.DomaineService;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.request.SousDomaineRequestDto;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.request.SousDomaineRequestDtoMapper;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.domaines.sousdomaine.repositories.SousDomaineRepository;
import ma.org.ormt.modules.domaines.sousdomaine.services.SousDomaineService;

@Service
public class SousDomaineServiceImpl extends BaseServiceImpl<SousDomaine> implements SousDomaineService {

    @Autowired
    private SousDomaineRepository sousDomaineRepository;

    @Autowired
    private DomaineService domaineService;

    @Autowired
    private ObjectsValidator<SousDomaineRequestDto> validator;

    @Autowired
    private SousDomaineRequestDtoMapper sousDomaineRequestMapper;

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
    public Optional<SousDomaine> findByTitre(String titre) {
        return sousDomaineRepository.findByTitre(titre);
    }

    @Override
    public Page<SousDomaine> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), SousDomaine.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<SousDomaine> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), SousDomaine.class);
        return findAll(specification, pageable);
    }

    @Override
    public SousDomaine create(SousDomaineRequestDto requestDto) {
        validator.validate(requestDto);
        Domaine domaine = domaineService.findById(requestDto.getIdDomaine())
                .orElseThrow(() -> new EntityNotFoundException(DOMAINE_NOT_FOUND));

        SousDomaine sousDomaineToCreate = sousDomaineRequestMapper.mapToEntity(requestDto);
        sousDomaineToCreate.setDomaine(domaine);
        return sousDomaineRepository.save(sousDomaineToCreate);
    }

    @Override
    public SousDomaine update(Long id, SousDomaineRequestDto requestDto) {
        validator.validate(requestDto);
        SousDomaine sousDomaineToUpdate = sousDomaineRequestMapper.mapToEntity(requestDto);
        checkPathId(id, sousDomaineToUpdate.getId());

        Domaine domaine = domaineService.findById(requestDto.getIdDomaine())
                .orElseThrow(() -> new EntityNotFoundException(DOMAINE_NOT_FOUND));

        SousDomaine sousDomaine = sousDomaineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

        updateFields(sousDomaine, sousDomaineToUpdate);
        sousDomaine.setDomaine(domaine);
        return sousDomaineRepository.save(sousDomaine);
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validateSousDomaineDependencies(id);
    }

    private void updateFields(SousDomaine sousDomaine, SousDomaine entityToUpdate) {
        sousDomaine.setTitre(entityToUpdate.getTitre());
        sousDomaine.setDescription(entityToUpdate.getDescription());
    }

    private void validateSousDomaineDependencies(Long id) {

    }

}