package ma.org.ancfcc.pva.modules.organisme.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.modules.organisme.Organisme;

public interface OrganismeRepository extends BaseRepository<Organisme> {

    Optional<Organisme> findByNom(String nom);

    @Query("SELECT m.code FROM Organisme o JOIN o.missions m WHERE o.id = :organismeId")
    List<String> findMissionCodesByOrganismeId(Long organismeId);

}