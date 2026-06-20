package ma.org.ormt.modules.dashboard.tbd.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.dashboard.tbd.models.TbdSection;

@Repository
public interface TbdSectionRepository extends BaseRepository<TbdSection> {

    List<TbdSection> findByDashboardIdOrderByOrdreAsc(Long dashboardId);

    List<TbdSection> findByDashboardIdAndActifTrue(Long dashboardId);
}
