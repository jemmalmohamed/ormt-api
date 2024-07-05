package ma.org.ancfcc.pva.modules.mission.scan.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.modules.mission.scan.ScanExecution;

public interface ScanExecutionRepository extends BaseRepository<ScanExecution> {

    @Query("SELECT COUNT(p) FROM ScanExecution p WHERE p.bande.id = :bandeId")
    long countByBandeId(@Param("bandeId") Long bandeId);
}