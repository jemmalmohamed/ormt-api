package ma.org.ancfcc.pva.modules.mission.bande.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.modules.mission.bande.Bande;

public interface BandeMetricsRepository extends BaseRepository<Bande> {

    @Query("SELECT ST_AsText(ST_Envelope(b.axePlanification)) FROM Bande b WHERE b.label = :label AND b.mission.id = :missionId")
    String getBandeBboxByLabelAndMission_Id(@Param("label") String label, @Param("missionId") Long missionId);

    @Query("SELECT ST_AsText(ST_Envelope(b.axePlanification)) FROM Bande b WHERE   b.mission.id = :missionId")
    String getBandeBBoxByMission_Id(@Param("missionId") Long missionId);

}