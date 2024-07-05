package ma.org.ormt.modules.mission.scan.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.mission.scan.ScanExecution;

public interface ScanExecutionRepository extends BaseRepository<ScanExecution> {

    @Query("SELECT COUNT(p) FROM ScanExecution p WHERE p.bande.id = :bandeId")
    long countByBandeId(@Param("bandeId") Long bandeId);
}