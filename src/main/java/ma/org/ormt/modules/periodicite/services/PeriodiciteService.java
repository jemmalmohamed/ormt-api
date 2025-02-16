package ma.org.ormt.modules.periodicite.services;

import java.util.Optional;
import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.periodicite.dtos.request.PeriodiciteRequestDto;
import ma.org.ormt.modules.periodicite.models.Periodicite;

public interface PeriodiciteService extends BaseService<Periodicite> {

    Optional<Periodicite> findByCode(String code);

    Page<Periodicite> getEntityList(QueryParams requestParams);

    Periodicite create(PeriodiciteRequestDto requestDto);

    Periodicite update(Long id, PeriodiciteRequestDto periodiciteRequestDto);

    boolean existsById(Long id);
}