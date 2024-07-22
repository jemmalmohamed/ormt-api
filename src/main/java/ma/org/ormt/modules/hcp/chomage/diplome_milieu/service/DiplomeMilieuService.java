package ma.org.ormt.modules.hcp.chomage.diplome_milieu.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.hcp.chomage.diplome_milieu.DiplomeMilieu;
import ma.org.ormt.modules.hcp.chomage.diplome_milieu.dto.request.DiplomeMilieuRequestDto;

public interface DiplomeMilieuService extends BaseService<DiplomeMilieu> {

    Optional<DiplomeMilieu> findByNom(String nom);

    Page<DiplomeMilieu> getEntityList(QueryParams requestParams);

    DiplomeMilieu create(DiplomeMilieuRequestDto requestDto);

    DiplomeMilieu update(Long id, DiplomeMilieuRequestDto diplomeMilieuRequestDto);

    boolean existsById(Long id);

}