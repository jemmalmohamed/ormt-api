package ma.org.ormt.modules.dashboard.tableaubord.v2.repositories;

import java.util.List;
import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;
import ma.org.ormt.modules.dashboard.tableaubord.v2.models.TableauBordV2Categorie;

public interface TableauBordV2CategorieRepository extends BaseRepository<TableauBordV2Categorie> {

    Optional<TableauBordV2Categorie> findByNom(String nom);

    Optional<TableauBordV2Categorie> findByTbDomaineAndNom(TBDomaine tbDomaine, String nom);

    List<TableauBordV2Categorie> findByActifTrueOrderByTbDomaineLibelleAscOrdreAscLibelleAsc();
}
