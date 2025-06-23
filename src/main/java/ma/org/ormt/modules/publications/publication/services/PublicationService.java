package ma.org.ormt.modules.publications.publication.services;

import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.publications.publication.dtos.request.PublicationRequestDto;
import ma.org.ormt.modules.publications.publication.models.Publication;

public interface PublicationService extends BaseService<Publication> {

    Optional<Publication> findByTitre(String titre);

    Page<Publication> getEntityList(QueryParams requestParams);

    Publication create(PublicationRequestDto requestDto) throws Exception;

    Publication update(Long id, PublicationRequestDto publicatPublicationRequestDto) throws Exception;

    boolean existsById(Long id);

}