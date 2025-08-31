package ma.org.ormt.modules.dashboard.tableaubord.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.dashboard.tableaubord.dtos.request.TableauBordRequestDto;
import ma.org.ormt.modules.dashboard.tableaubord.models.TableauBord;

public interface TableauBordService extends BaseService<TableauBord> {

    Optional<TableauBord> findByNom(String nom);

    Page<TableauBord> getEntityList(QueryParams requestParams);

    Page<TableauBord> getEntitiesByIds(List<Long> ids, QueryParams requestParams);

    TableauBord create(TableauBordRequestDto requestDto) throws Exception;

    TableauBord update(Long id, TableauBordRequestDto requestDto) throws Exception;

    TableauBord save(TableauBord tableauBord);

    boolean existsById(Long id);
}
