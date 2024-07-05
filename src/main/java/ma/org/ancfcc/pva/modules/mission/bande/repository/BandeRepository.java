package ma.org.ancfcc.pva.modules.mission.bande.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.modules.mission.bande.Bande;

public interface BandeRepository extends BaseRepository<Bande> {

    Optional<Bande> findByNom(String nom);

    Optional<Bande> findByLabel(String label);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Bande b WHERE b.label = :label AND b.mission.id = :missionId")
    boolean existsByLabelAndMissionId(@Param("label") String label, @Param("missionId") Long missionId);

    @Query("SELECT b FROM Bande b WHERE b.label = :label AND b.mission.id = :missionId")
    Optional<Bande> findBandeByLabelAndMissionId(@Param("label") String label, @Param("missionId") Long missionId);

    @Query("DELETE FROM Bande b WHERE b.mission.id = :missionId")
    void deleteAllByMissionId(@Param("missionId") Long missionId);
}