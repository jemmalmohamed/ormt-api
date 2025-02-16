package ma.org.ormt.modules.periodicite.repository;

import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.periodicite.Periodicite;

public interface PeriodiciteRepository extends BaseRepository<Periodicite> {
    Optional<Periodicite> findByCode(String code);
}