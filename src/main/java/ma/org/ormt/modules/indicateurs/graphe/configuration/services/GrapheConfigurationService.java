package ma.org.ormt.modules.indicateurs.graphe.configuration.services;

import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.indicateurs.graphe.configuration.dtos.request.GrapheConfigurationRequestDto;
import ma.org.ormt.modules.indicateurs.graphe.configuration.models.GrapheConfiguration;

public interface GrapheConfigurationService extends BaseService<GrapheConfiguration> {

    Optional<GrapheConfiguration> findByNom(String nom);

    Page<GrapheConfiguration> getEntityList(QueryParams requestParams);

    GrapheConfiguration create(GrapheConfigurationRequestDto requestDto);

    GrapheConfiguration update(Long id, GrapheConfigurationRequestDto grapheConfigurationRequestDto);

    GrapheConfiguration save(GrapheConfiguration grapheConfiguration);

    boolean existsById(Long id);

    Optional<GrapheConfiguration> findByIndicateurAndGrapheType(Long indicateurId, String grapheType);

    GrapheConfiguration setDefaultGrapheConfiguration(Long indicateurId, Long grapheConfigurationId);

}