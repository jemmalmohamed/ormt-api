package ma.org.ormt.modules.dashboard.tableaubord.v2.repositories;

import java.util.List;
import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.dashboard.tableaubord.v2.enums.TableauBordV2Status;
import ma.org.ormt.modules.dashboard.tableaubord.v2.models.TableauBordV2;
import ma.org.ormt.modules.dashboard.tableaubord.v2.models.TableauBordV2Categorie;

public interface TableauBordV2Repository extends BaseRepository<TableauBordV2> {

    Optional<TableauBordV2> findByNom(String nom);

    Optional<TableauBordV2> findByNomAndStatusAndActifTrue(String nom, TableauBordV2Status status);

    List<TableauBordV2> findByStatusAndActifTrueOrderByCategorieOrdreAscTitreAsc(TableauBordV2Status status);

    List<TableauBordV2> findByIdInAndStatusAndActifTrueOrderByCategorieOrdreAscTitreAsc(List<Long> ids,
            TableauBordV2Status status);

    boolean existsByCategorieAndActifTrueAndStatusNotAndIdNot(TableauBordV2Categorie categorie,
            TableauBordV2Status status, Long id);
}
