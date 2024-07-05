package ma.org.ormt.modules.objet.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.objet.Objet;
import ma.org.ormt.modules.objet.dto.request.ObjetRequestDto;

public interface ObjetService extends BaseService<Objet> {

    Optional<Objet> findByNom(String nom);

    Page<Objet> getEntityList(QueryParams requestParams);

    Objet create(ObjetRequestDto requestDto);

    Objet update(Long id, ObjetRequestDto objetRequestDto);

    boolean existsById(Long id);

}