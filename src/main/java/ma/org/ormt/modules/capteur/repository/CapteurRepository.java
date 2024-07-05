package ma.org.ormt.modules.capteur.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.capteur.Capteur;

public interface CapteurRepository extends BaseRepository<Capteur> {

    Optional<Capteur> findByNom(String nom);

    Optional<Capteur> findByCode(String code);

    @Query("SELECT m.code FROM Capteur o JOIN o.missions m WHERE o.id = :capteurId")
    List<String> findMissionCodesByCapteurId(Long capteurId);

}