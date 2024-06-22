package ma.org.ancfcc.pva.modules.planaction.repository;

import java.util.Optional;

import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.modules.planaction.PlanAction;

public interface PlanActionRepository extends BaseRepository<PlanAction> {

    Optional<PlanAction> findByNom(String nom);

}