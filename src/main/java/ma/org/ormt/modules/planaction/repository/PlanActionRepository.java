package ma.org.ormt.modules.planaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.planaction.PlanAction;

public interface PlanActionRepository extends BaseRepository<PlanAction> {

    Optional<PlanAction> findByNom(String nom);

    @Query("SELECT m.nom FROM PlanAction o JOIN o.missions m WHERE o.id = :planActionId")
    List<String> findMissionCodesByPLanActionId(Long planActionId);
}