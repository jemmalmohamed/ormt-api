package ma.org.ormt.modules.espaces.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.espaces.dtos.request.EspaceRequestDto;
import ma.org.ormt.modules.espaces.models.Espace;

public interface EspaceService extends BaseService<Espace> {

    Optional<Espace> findByNom(String nom);

    Page<Espace> getEntityList(QueryParams requestParams);

    public Page<Espace> getEntitiesByIds(List<Long> ids, QueryParams params);

    Espace create(EspaceRequestDto requestDto);

    Espace update(Long id, EspaceRequestDto espaceRequestDto);

    Espace save(Espace espace);

    boolean existsById(Long id);

    void attachDomaine(Long espaceId, Long domaineId);

    void detachDomaine(Long eppaceDomaineId);

}