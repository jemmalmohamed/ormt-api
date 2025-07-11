package ma.org.ormt.modules.indicateurs.graphe.type.services;

import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.indicateurs.graphe.type.models.GrapheType;

public interface GrapheTypeService extends BaseService<GrapheType> {

    Optional<GrapheType> findByNom(String nom);

    Page<GrapheType> getEntityList(QueryParams requestParams);

    boolean existsById(Long id);

}