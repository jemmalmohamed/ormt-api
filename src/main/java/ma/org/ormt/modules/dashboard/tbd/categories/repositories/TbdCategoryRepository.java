package ma.org.ormt.modules.dashboard.tbd.categories.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;
import ma.org.ormt.modules.dashboard.tbd.categories.models.TbdCategory;

public interface TbdCategoryRepository extends BaseRepository<TbdCategory> {

    Optional<TbdCategory> findByNom(String nom);

    Optional<TbdCategory> findByTbDomaineAndNom(TBDomaine tbDomaine, String nom);

    List<TbdCategory> findByActifTrueOrderByTbDomaineLibelleAscOrdreAscLibelleAsc();

    List<TbdCategory> findByIdInAndActifTrueOrderByTbDomaineLibelleAscOrdreAscLibelleAsc(Collection<Long> ids);
}
