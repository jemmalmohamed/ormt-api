package ma.org.ancfcc.pva.modules.basemap.repository;

import java.util.Optional;

import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.modules.basemap.Basemap;

public interface BasemapRepository extends BaseRepository<Basemap> {

    Optional<Basemap> findByNom(String nom);

}