package ma.org.ormt.modules.indicateurs.source.services.impl;

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
import ma.org.ormt.modules.indicateurs.source.dtos.request.SourceRequestDto;
import ma.org.ormt.modules.indicateurs.source.dtos.request.SourceRequestDtoMapper;
import ma.org.ormt.modules.indicateurs.source.models.Source;
import ma.org.ormt.modules.indicateurs.source.repositories.SourceRepository;
import ma.org.ormt.modules.indicateurs.source.services.SourceService;

@Service
@Transactional
public class SourceServiceImpl extends BaseServiceImpl<Source> implements SourceService {

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private ObjectsValidator<SourceRequestDto> validator;

    @Autowired
    private SourceRequestDtoMapper sourceRequestMapper;

    private static final String NOT_FOUND_STRING = "Source non trouvée";

    public SourceServiceImpl(SourceRepository sourceRepository, SpecificationService specificationService) {
        super(sourceRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return sourceRepository.existsById(id);
    }

    @Override
    public Optional<Source> findByNom(String nom) {
        return sourceRepository.findByNom(nom);
    }

    @Override
    public Page<Source> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Source.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Source> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Source.class);
        return findAll(specification, pageable);
    }

    @Override
    public Source save(Source source) {
        return sourceRepository.save(source);
    }

    @Override
    public Source create(SourceRequestDto requestDto) {
        try {
            validator.validate(requestDto);
            Source sourceToCreate = sourceRequestMapper.mapToEntity(requestDto);
            return sourceRepository.save(sourceToCreate);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erreur lors de la création de la source: " + e.getMessage());
        }
    }

    @Override
    public Source update(Long id, SourceRequestDto requestDto) {
        try {
            validator.validate(requestDto);
            checkPathId(id, requestDto.getId());

            Source source = sourceRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

            updateFields(source, requestDto);
            return sourceRepository.save(source);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erreur lors de la mise à jour de la source: " + e.getMessage());
        }
    }

    private void updateFields(Source source, SourceRequestDto entityToUpdate) {
        source.setNom(entityToUpdate.getNom());
        source.setDescription(entityToUpdate.getDescription());
    }

}