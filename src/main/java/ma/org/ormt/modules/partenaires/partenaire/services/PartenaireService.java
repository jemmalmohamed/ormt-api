package ma.org.ormt.modules.partenaires.partenaire.services;

import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.partenaires.partenaire.dtos.request.PartenaireRequestDto;
import ma.org.ormt.modules.partenaires.partenaire.models.Partenaire;

public interface PartenaireService extends BaseService<Partenaire> {

    Optional<Partenaire> findByNom(String nom);

    Page<Partenaire> getEntityList(QueryParams requestParams);

    Partenaire create(PartenaireRequestDto requestDto);

    Partenaire update(Long id, PartenaireRequestDto partenaireRequestDto);

    boolean existsById(Long id);

}