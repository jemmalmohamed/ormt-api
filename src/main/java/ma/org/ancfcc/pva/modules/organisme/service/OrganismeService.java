package ma.org.ancfcc.pva.modules.organisme.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;

import ma.org.ancfcc.pva.core.commun.base.service.BaseService;
import ma.org.ancfcc.pva.core.commun.rest.queries.QueryParams;
import ma.org.ancfcc.pva.modules.organisme.Organisme;
import ma.org.ancfcc.pva.modules.organisme.dto.request.OrganismeRequestDto;

public interface OrganismeService extends BaseService<Organisme> {

    Optional<Organisme> findByNom(String nom);

    Page<Organisme> getEntityList(QueryParams requestParams);

    Organisme create(OrganismeRequestDto requestDto);

    Organisme update(UUID id, OrganismeRequestDto organismeRequestDto);

    boolean existsById(UUID id);

    List<String> findMissionCodesByOrganismeId(UUID organismeId);

}