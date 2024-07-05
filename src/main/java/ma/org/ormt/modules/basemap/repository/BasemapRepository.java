package ma.org.ormt.modules.basemap.repository;

import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.basemap.Basemap;

public interface BasemapRepository extends BaseRepository<Basemap> {

    Optional<Basemap> findByNom(String nom);

}