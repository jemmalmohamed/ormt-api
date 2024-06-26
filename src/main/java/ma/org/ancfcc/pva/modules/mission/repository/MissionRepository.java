package ma.org.ancfcc.pva.modules.mission.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.modules.mission.Mission;

public interface MissionRepository extends BaseRepository<Mission> {

    Optional<Mission> findByCode(String code);

    Optional<Mission> findByNom(String nom);

    boolean existsByCode(String code);

    boolean existsByIdAndDelimitationIsNotNull(UUID id);

    @Query("SELECT m FROM Mission m " +
            "LEFT JOIN FETCH m.objets o " +
            "LEFT JOIN FETCH m.organisme org " +
            "LEFT JOIN FETCH m.planAction pa " +
            "WHERE m.id = :id")
    Optional<Mission> findByIdWithDetails(@Param("id") UUID id);

}