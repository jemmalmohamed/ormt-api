package ma.org.ormt.modules.indicateurs.graphe.configuration.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.indicateurs.graphe.configuration.models.GrapheConfiguration;
import ma.org.ormt.modules.indicateurs.graphe.type.models.GrapheType;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Repository
public interface GrapheConfigurationRepository extends BaseRepository<GrapheConfiguration> {

    Optional<GrapheConfiguration> findByNom(String nom);

    Optional<GrapheConfiguration> findByIndicateurAndGrapheType(
            Indicateur indicateur,
            GrapheType grapheType);

    List<GrapheConfiguration> findByIndicateurIdAndIsDefaultTrue(Long indicateurId);

}