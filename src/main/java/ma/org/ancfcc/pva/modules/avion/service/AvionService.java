package ma.org.ancfcc.pva.modules.avion.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ancfcc.pva.core.commun.base.service.BaseService;
import ma.org.ancfcc.pva.core.commun.rest.queries.QueryParams;
import ma.org.ancfcc.pva.modules.avion.Avion;
import ma.org.ancfcc.pva.modules.avion.dto.request.AvionRequestDto;

public interface AvionService extends BaseService<Avion> {

    Optional<Avion> findByMatricule(String matricule);

    Page<Avion> getEntityList(QueryParams requestParams);

    Avion create(AvionRequestDto requestDto);

    Avion update(Long id, AvionRequestDto avionRequestDto);

    boolean existsById(Long id);

}