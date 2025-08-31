package ma.org.ormt.modules.dashboard.tableaubord.services.impl;

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
import ma.org.ormt.modules.dashboard.tableaubord.dtos.request.TableauBordRequestDto;
import ma.org.ormt.modules.dashboard.tableaubord.dtos.request.TableauBordRequestDtoMapper;
import ma.org.ormt.modules.dashboard.tableaubord.models.TableauBord;
import ma.org.ormt.modules.dashboard.tableaubord.repositories.TableauBordRepository;
import ma.org.ormt.modules.dashboard.tableaubord.services.TableauBordService;

@Service
@Transactional
public class TableauBordServiceImpl extends BaseServiceImpl<TableauBord> implements TableauBordService {

    @Autowired
    private TableauBordRepository tableauBordRepository;

    @Autowired
    private ObjectsValidator<TableauBordRequestDto> validator;

    @Autowired
    private TableauBordRequestDtoMapper requestMapper;

    private static final String NOT_FOUND_STRING = "Tableau de bord non trouvé";

    public TableauBordServiceImpl(TableauBordRepository repository, SpecificationService specificationService) {
        super(repository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return super.existsById(id);
    }

    @Override
    public Optional<TableauBord> findByNom(String nom) {
        return tableauBordRepository.findByNom(nom);
    }

    @Override
    public Page<TableauBord> getEntityList(QueryParams requestParams) {
        return super.getEntityList(requestParams, TableauBord.class);
    }

    @Override
    public Page<TableauBord> getEntitiesByIds(List<Long> ids, QueryParams requestParams) {
        return super.getEntitiesByIds(ids, requestParams, TableauBord.class);
    }

    @Override
    public TableauBord save(TableauBord tableauBord) {
        return tableauBordRepository.save(tableauBord);
    }

    @Override
    public TableauBord create(TableauBordRequestDto requestDto) throws Exception {
        validator.validate(requestDto);
        TableauBord entity = requestMapper.mapToEntity(requestDto);
        return tableauBordRepository.save(entity);
    }

    @Override
    public TableauBord update(Long id, TableauBordRequestDto requestDto) throws Exception {
        validator.validate(requestDto);
        checkPathId(id, requestDto.getId());
        TableauBord tb = tableauBordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        tb.setNom(requestDto.getNom());
        tb.setDescription(requestDto.getDescription());
        tb.setActif(requestDto.getActif());
        return tableauBordRepository.save(tb);
    }
}
