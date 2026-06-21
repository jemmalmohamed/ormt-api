package ma.org.ormt.modules.dashboard.tbd.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.dashboard.tbd.models.TbdDashboard;

@Repository
public interface TbdDashboardRepository extends BaseRepository<TbdDashboard> {

    Page<TbdDashboard> findByActifTrueOrderByLastModifiedDateDesc(Pageable pageable);

    List<TbdDashboard> findByStatusAndActifTrue(String status);

    Optional<TbdDashboard> findByIdAndActifTrue(Long id);

    Optional<TbdDashboard> findByNomIgnoreCaseAndActifTrue(String nom);

    Optional<TbdDashboard> findByTitreIgnoreCaseAndActifTrue(String titre);
}
