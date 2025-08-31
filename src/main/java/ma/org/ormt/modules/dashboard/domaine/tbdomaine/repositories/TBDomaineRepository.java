package ma.org.ormt.modules.dashboard.domaine.tbdomaine.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;

public interface TBDomaineRepository extends BaseRepository<TBDomaine> {

    Optional<TBDomaine> findByNom(String nom);

    @Query("SELECT CASE WHEN COUNT(td) > 0 THEN true ELSE false END FROM TBDomaine d JOIN TableauBordDomaine td ON td.tbDomaine.id = d.id WHERE d.id = :id AND td.tableauBord.id = :tableaubordId")
    boolean existsByIdAndTableaubordDomainesTableaubordId(@Param("id") Long id,
            @Param("tableaubordId") Long tableaubordId);

    @Query("SELECT d.id FROM TBDomaine d JOIN TableauBordDomaine td ON td.tbDomaine.id = d.id WHERE td.tableauBord.id = :tableaubordId")
    List<Long> findIdsByTableaubordId(@Param("tableaubordId") Long tableaubordId);

}