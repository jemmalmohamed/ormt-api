package ma.org.ormt.modules.dashboard.tbd.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.dashboard.tbd.models.TbdAssignation;

@Repository
public interface TbdAssignationRepository extends BaseRepository<TbdAssignation> {

    Optional<TbdAssignation> findByDashboardId(Long dashboardId);

    @Transactional
    void deleteByDashboardId(Long dashboardId);
}
