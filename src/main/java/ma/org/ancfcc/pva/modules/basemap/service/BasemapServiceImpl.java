package ma.org.ancfcc.pva.modules.basemap.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ancfcc.pva.core.commun.base.service.BaseServiceImpl;
import ma.org.ancfcc.pva.core.commun.base.service.SpecificationService;
import ma.org.ancfcc.pva.core.commun.rest.queries.QueryParams;
import ma.org.ancfcc.pva.core.utilities.EntityInspector;
import ma.org.ancfcc.pva.core.utilities.PaginationUtils;
import ma.org.ancfcc.pva.core.validators.ObjectsValidator;
import ma.org.ancfcc.pva.modules.basemap.Basemap;
import ma.org.ancfcc.pva.modules.basemap.dto.request.BasemapRequestDto;
import ma.org.ancfcc.pva.modules.basemap.dto.request.BasemapRequestMapper;
import ma.org.ancfcc.pva.modules.basemap.repository.BasemapRepository;

@Service
public class BasemapServiceImpl extends BaseServiceImpl<Basemap> implements BasemapService {

    @Autowired
    private BasemapRepository basemapRepository;

    @Autowired
    private ObjectsValidator<BasemapRequestDto> validator;

    @Autowired
    private BasemapRequestMapper basemapRequestMapper;

    static final String NOT_FOUND_STRING = "Basemap not found";

    public BasemapServiceImpl(BasemapRepository basemapRepository, SpecificationService specificationService) {
        super(basemapRepository, specificationService);
    }

    @Override
    public boolean existsById(UUID id) {
        return basemapRepository.existsById(id);
    }

    @Override
    public Optional<Basemap> findByNom(String nom) {
        return basemapRepository.findByNom(nom);
    }

    @Override
    public Page<Basemap> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Basemap.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Basemap> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Basemap.class);
        return findAll(specification, pageable);
    }

    @Override
    public Basemap create(BasemapRequestDto requestDto) {
        validator.validate(requestDto);
        Basemap basemapToCreate = basemapRequestMapper.mapToEntity(requestDto);
        return basemapRepository.save(basemapToCreate);
    }

    @Override
    public Basemap update(UUID id, BasemapRequestDto requestDto) {
        // verify if id is the same as the one in the body
        validator.validate(requestDto);
        Basemap basemapToUpdate = basemapRequestMapper.mapToEntity(requestDto);
        checkPathId(id, basemapToUpdate.getId());
        Basemap basemap = basemapRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateFields(basemap, basemapToUpdate);
        return basemapRepository.save(basemap);
    }

    @Override
    public void validateBeforeDelete(UUID id) {
        validateMissionDependencies(id);
    }

    private void updateFields(Basemap basemap, Basemap entityToUpdate) {
        basemap.setNom(entityToUpdate.getNom());
        basemap.setUrl(entityToUpdate.getUrl());

    }

    private void validateMissionDependencies(UUID id) {
        // TODO : uncomment this code after implementing the mission module
    }

}