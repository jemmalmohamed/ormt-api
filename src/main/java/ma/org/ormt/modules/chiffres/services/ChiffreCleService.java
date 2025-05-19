package ma.org.ormt.modules.chiffres.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.chiffres.dtos.request.ChiffreCleRequestDto;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;

public interface ChiffreCleService extends BaseService<ChiffreCle> {

    Optional<ChiffreCle> findByLibelle(String libelle);

    Page<ChiffreCle> getEntityList(QueryParams requestParams);

    public Page<ChiffreCle> getEntitiesByIds(List<Long> ids, QueryParams params);

    ChiffreCle create(ChiffreCleRequestDto requestDto) throws Exception;

    ChiffreCle update(Long id, ChiffreCleRequestDto chiffrecleRequestDto) throws Exception;

    ChiffreCle save(ChiffreCle chiffrecle);

    boolean existsById(Long id);

    void attachDomaine(Long chiffrecleId, Long domaineId);

    void detachDomaine(Long eppaceDomaineId);

}