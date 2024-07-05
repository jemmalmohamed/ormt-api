package ma.org.ancfcc.pva.modules.objet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.modules.objet.Objet;

public interface ObjetRepository extends BaseRepository<Objet> {

    Optional<Objet> findByNom(String nom);

    @Query("SELECT m.code FROM Mission m JOIN m.objets o WHERE o.id = :objetId")
    List<String> findMissionCodesByObjetsId(Long objetId);
}