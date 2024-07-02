package ma.org.ancfcc.pva.modules.mission.bande.repository;

import java.util.Optional;
import java.util.UUID;

import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.modules.mission.bande.Bande;

public interface BandeRepository extends BaseRepository<Bande> {

    Optional<Bande> findByNom(String nom);

    Optional<Bande> findByLabel(String label);

    boolean existsByLabelAndMission_Id(String label, UUID missionId);

    Optional<Bande> findBandeByLabelAndMission_Id(String label, UUID missionId);

    void deleteAllByMission_Id(UUID missionId);

}