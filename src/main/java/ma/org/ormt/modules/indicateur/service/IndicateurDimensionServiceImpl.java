package ma.org.ormt.modules.indicateur.service;

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
import ma.org.ormt.modules.indicateur.dto.request.IndicateurDimensionRequestDto;
import ma.org.ormt.modules.indicateur.dto.request.IndicateurDimensionRequestDtoMapper;
import ma.org.ormt.modules.indicateur.models.IndicateurDimension;
import ma.org.ormt.modules.indicateur.repository.IndicateurDimensionRepository;

@Service
public class IndicateurDimensionServiceImpl extends BaseServiceImpl<IndicateurDimension>
        implements IndicateurDimensionService {

    @Autowired
    private IndicateurDimensionRepository indicateurDimensionRepository;

    @Autowired
    private ObjectsValidator<IndicateurDimensionRequestDto> validator;

    @Autowired
    private IndicateurDimensionRequestDtoMapper indicateurDimensionRequestMapper;

    static final String NOT_FOUND_STRING = "IndicateurDimension not found";

    public IndicateurDimensionServiceImpl(IndicateurDimensionRepository indicateurDimensionRepository,
            SpecificationService specificationService) {
        super(indicateurDimensionRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return indicateurDimensionRepository.existsById(id);
    }

    @Override
    public Page<IndicateurDimension> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), IndicateurDimension.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<IndicateurDimension> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), IndicateurDimension.class);
        return findAll(specification, pageable);
    }

    @Override
    public IndicateurDimension create(IndicateurDimensionRequestDto requestDto) {
        validator.validate(requestDto);
        IndicateurDimension indicateurDimensionToCreate = indicateurDimensionRequestMapper.mapToEntity(requestDto);
        return indicateurDimensionRepository.save(indicateurDimensionToCreate);
    }

    @Override
    public IndicateurDimension update(Long id, IndicateurDimensionRequestDto requestDto) {
        validator.validate(requestDto);
        IndicateurDimension indicateurDimensionToUpdate = indicateurDimensionRequestMapper.mapToEntity(requestDto);
        checkPathId(id, indicateurDimensionToUpdate.getId());
        IndicateurDimension indicateurDimension = indicateurDimensionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateFields(indicateurDimension, indicateurDimensionToUpdate);
        return indicateurDimensionRepository.save(indicateurDimension);
    }

    private void updateFields(IndicateurDimension indicateurDimension, IndicateurDimension entityToUpdate) {
        indicateurDimension.setNom(entityToUpdate.getNom());
        indicateurDimension.setType(entityToUpdate.getType());
        indicateurDimension.setDescription(entityToUpdate.getDescription());
    }
}