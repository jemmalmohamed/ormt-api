package ma.org.ormt.modules.domaines.sousdomaine.services;

import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.request.SousDomaineRequestDto;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;

public interface SousDomaineService extends BaseService<SousDomaine> {

    Optional<SousDomaine> findByTitre(String titre);

    Page<SousDomaine> getEntityList(QueryParams requestParams);

    SousDomaine create(SousDomaineRequestDto requestDto);

    SousDomaine update(Long id, SousDomaineRequestDto sousDomaineRequestDto);

    boolean existsById(Long id);

}