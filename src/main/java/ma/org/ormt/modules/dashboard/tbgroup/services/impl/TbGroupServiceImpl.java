package ma.org.ormt.modules.dashboard.tbgroup.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.dashboard.tbgroup.dtos.request.TbGroupRequestDto;
import ma.org.ormt.modules.dashboard.tbgroup.dtos.request.TbGroupRequestDtoMapper;
import ma.org.ormt.modules.dashboard.tbgroup.models.TbGroup;
import ma.org.ormt.modules.dashboard.tbgroup.repositories.TbGroupRepository;
import ma.org.ormt.modules.dashboard.tbgroup.services.TbGroupService;

@Service
@Transactional
public class TbGroupServiceImpl extends BaseServiceImpl<TbGroup> implements TbGroupService {

    @Autowired
    private TbGroupRepository tbGroupRepository;

    @Autowired
    private ObjectsValidator<TbGroupRequestDto> validator;

    @Autowired
    private TbGroupRequestDtoMapper requestMapper;

    private static final String NOT_FOUND_STRING = "TB group non trouvé";

    public TbGroupServiceImpl(TbGroupRepository repository, SpecificationService specificationService) {
        super(repository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return super.existsById(id);
    }

    @Override
    public Optional<TbGroup> findByNom(String nom) {
        return tbGroupRepository.findByNom(nom);
    }

    @Override
    public Page<TbGroup> getEntityList(QueryParams requestParams) {
        return super.getEntityList(requestParams, TbGroup.class);
    }

    @Override
    public Page<TbGroup> getEntitiesByIds(List<Long> ids, QueryParams requestParams) {
        return super.getEntitiesByIds(ids, requestParams, TbGroup.class);
    }

    @Override
    public TbGroup save(TbGroup tbGroup) {
        return tbGroupRepository.save(tbGroup);
    }

    @Override
    public TbGroup create(TbGroupRequestDto requestDto) throws Exception {
        validator.validate(requestDto);
        TbGroup entity = requestMapper.mapToEntity(requestDto);
        return tbGroupRepository.save(entity);
    }

    @Override
    public TbGroup update(Long id, TbGroupRequestDto requestDto) throws Exception {
        validator.validate(requestDto);
        checkPathId(id, requestDto.getId());
        TbGroup tb = tbGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        tb.setNom(requestDto.getNom());
        tb.setDescription(requestDto.getDescription());
        tb.setActif(requestDto.getActif());
        return tbGroupRepository.save(tb);
    }
}
