package ma.org.ormt.modules.indicateurs.graphe.configuration.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.indicateurs.graphe.configuration.models.GrapheConfiguration;

@Repository
public interface GrapheConfigurationRepository extends BaseRepository<GrapheConfiguration> {

    Optional<GrapheConfiguration> findByNom(String nom);

}