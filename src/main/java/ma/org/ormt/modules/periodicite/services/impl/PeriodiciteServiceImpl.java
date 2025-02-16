package ma.org.ormt.modules.periodicite.services.impl;

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
import ma.org.ormt.modules.periodicite.dtos.request.PeriodiciteRequestDto;
import ma.org.ormt.modules.periodicite.dtos.request.PeriodiciteRequestMapper;
import ma.org.ormt.modules.periodicite.models.Periodicite;
import ma.org.ormt.modules.periodicite.repositories.PeriodiciteRepository;
import ma.org.ormt.modules.periodicite.services.PeriodiciteService;

@Service
public class PeriodiciteServiceImpl extends BaseServiceImpl<Periodicite> implements PeriodiciteService {

    @Autowired
    private PeriodiciteRepository periodiciteRepository;

    @Autowired
    private ObjectsValidator<PeriodiciteRequestDto> validator;

    @Autowired
    private PeriodiciteRequestMapper periodiciteRequestMapper;

    static final String NOT_FOUND_STRING = "Periodicite not found";

    public PeriodiciteServiceImpl(PeriodiciteRepository periodiciteRepository,
            SpecificationService specificationService) {
        super(periodiciteRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return periodiciteRepository.existsById(id);
    }

    @Override
    public Optional<Periodicite> findByCode(String code) {
        return periodiciteRepository.findByCode(code);
    }

    @Override
    public Page<Periodicite> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Periodicite.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Periodicite> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Periodicite.class);
        return findAll(specification, pageable);
    }

    @Override
    public Periodicite create(PeriodiciteRequestDto requestDto) {
        validator.validate(requestDto);
        Periodicite periodiciteToCreate = periodiciteRequestMapper.mapToEntity(requestDto);
        return periodiciteRepository.save(periodiciteToCreate);
    }

    @Override
    public Periodicite update(Long id, PeriodiciteRequestDto requestDto) {
        validator.validate(requestDto);
        Periodicite periodiciteToUpdate = periodiciteRequestMapper.mapToEntity(requestDto);
        checkPathId(id, periodiciteToUpdate.getId());
        Periodicite periodicite = periodiciteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateFields(periodicite, periodiciteToUpdate);
        return periodiciteRepository.save(periodicite);
    }

    private void updateFields(Periodicite periodicite, Periodicite entityToUpdate) {
        periodicite.setCode(entityToUpdate.getCode());
        periodicite.setLibelle(entityToUpdate.getLibelle());
    }
}