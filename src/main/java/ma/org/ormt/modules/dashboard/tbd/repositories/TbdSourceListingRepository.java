package ma.org.ormt.modules.dashboard.tbd.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.dashboard.tbd.models.TbdSourceListing;

@Repository
public interface TbdSourceListingRepository extends BaseRepository<TbdSourceListing> {

    List<TbdSourceListing> findByDashboardIdOrderByOrdreAsc(Long dashboardId);

    @Transactional
    void deleteByDashboardId(Long dashboardId);
}
