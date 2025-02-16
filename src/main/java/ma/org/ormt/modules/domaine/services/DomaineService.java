package ma.org.ormt.modules.domaine.services;

import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.domaine.dtos.request.DomaineRequestDto;
import ma.org.ormt.modules.domaine.models.Domaine;

public interface DomaineService extends BaseService<Domaine> {

    Optional<Domaine> findByTitre(String titre);

    Page<Domaine> getEntityList(QueryParams requestParams);

    Domaine create(DomaineRequestDto requestDto);

    Domaine update(Long id, DomaineRequestDto domaineRequestDto);

    boolean existsById(Long id);

    void addSousDomaine(Long domaineId, Long sousDomaineId);

}