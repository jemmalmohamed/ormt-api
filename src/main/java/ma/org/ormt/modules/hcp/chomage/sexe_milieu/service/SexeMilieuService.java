package ma.org.ormt.modules.hcp.chomage.sexe_milieu.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.hcp.chomage.sexe_milieu.SexeMilieu;
import ma.org.ormt.modules.hcp.chomage.sexe_milieu.dto.request.SexeMilieuRequestDto;

public interface SexeMilieuService extends BaseService<SexeMilieu> {

    Optional<SexeMilieu> findByNom(String nom);

    Page<SexeMilieu> getEntityList(QueryParams requestParams);

    SexeMilieu create(SexeMilieuRequestDto requestDto);

    SexeMilieu update(Long id, SexeMilieuRequestDto sexeMilieuRequestDto);

    boolean existsById(Long id);

}