package ma.org.ormt.modules.periodicite.service;

import java.util.Optional;
import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.periodicite.Periodicite;
import ma.org.ormt.modules.periodicite.dto.request.PeriodiciteRequestDto;

public interface PeriodiciteService extends BaseService<Periodicite> {

    Optional<Periodicite> findByCode(String code);

    Page<Periodicite> getEntityList(QueryParams requestParams);

    Periodicite create(PeriodiciteRequestDto requestDto);

    Periodicite update(Long id, PeriodiciteRequestDto periodiciteRequestDto);

    boolean existsById(Long id);
}