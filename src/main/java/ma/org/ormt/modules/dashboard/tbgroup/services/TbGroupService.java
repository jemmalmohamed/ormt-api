package ma.org.ormt.modules.dashboard.tbgroup.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.dashboard.tbgroup.dtos.request.TbGroupRequestDto;
import ma.org.ormt.modules.dashboard.tbgroup.models.TbGroup;

public interface TbGroupService extends BaseService<TbGroup> {

    Optional<TbGroup> findByNom(String nom);

    Page<TbGroup> getEntityList(QueryParams requestParams);

    Page<TbGroup> getEntitiesByIds(List<Long> ids, QueryParams requestParams);

    TbGroup create(TbGroupRequestDto requestDto) throws Exception;

    TbGroup update(Long id, TbGroupRequestDto requestDto) throws Exception;

    TbGroup save(TbGroup tbGroup);

    boolean existsById(Long id);
}
