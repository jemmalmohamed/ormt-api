package ma.org.ormt.modules.organisme.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.organisme.Organisme;
import ma.org.ormt.modules.organisme.dto.request.OrganismeRequestDto;

public interface OrganismeService extends BaseService<Organisme> {

    Optional<Organisme> findByNom(String nom);

    Page<Organisme> getEntityList(QueryParams requestParams);

    Organisme create(OrganismeRequestDto requestDto);

    Organisme update(Long id, OrganismeRequestDto organismeRequestDto);

    boolean existsById(Long id);

}