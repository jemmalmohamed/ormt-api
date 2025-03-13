package ma.org.ormt.modules.indicateurs.source.services;

import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.indicateurs.source.dtos.request.SourceRequestDto;
import ma.org.ormt.modules.indicateurs.source.models.Source;

public interface SourceService extends BaseService<Source> {

    Optional<Source> findByNom(String nom);

    Page<Source> getEntityList(QueryParams requestParams);

    Source create(SourceRequestDto requestDto);

    Source update(Long id, SourceRequestDto sourceRequestDto);

    Source save(Source source);

    boolean existsById(Long id);

}