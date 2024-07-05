package ma.org.ormt.modules.avion.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.avion.Avion;
import ma.org.ormt.modules.avion.dto.request.AvionRequestDto;

public interface AvionService extends BaseService<Avion> {

    Optional<Avion> findByMatricule(String matricule);

    Page<Avion> getEntityList(QueryParams requestParams);

    Avion create(AvionRequestDto requestDto);

    Avion update(Long id, AvionRequestDto avionRequestDto);

    boolean existsById(Long id);

}