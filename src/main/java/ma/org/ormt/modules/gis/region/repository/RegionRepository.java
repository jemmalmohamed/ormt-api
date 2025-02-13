package ma.org.ormt.modules.gis.region.repository;

import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.gis.region.Region;

public interface RegionRepository extends BaseRepository<Region> {

    Optional<Region> findByNom(String nom);

    boolean existsByNom(String code);

    boolean existsByIdAndDelimitationIsNotNull(Long id);
}