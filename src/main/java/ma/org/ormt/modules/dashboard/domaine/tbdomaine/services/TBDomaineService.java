package ma.org.ormt.modules.dashboard.domaine.tbdomaine.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.details.TBDomaineDetailDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.request.TBDomaineRequestDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;

public interface TBDomaineService extends BaseService<TBDomaine> {

    Optional<TBDomaine> findByNom(String nom);

    Page<TBDomaine> getEntityList(QueryParams requestParams);

    Page<TBDomaine> getEntitiesByIds(List<Long> ids, QueryParams requestParams);

    TBDomaine create(TBDomaineRequestDto requestDto) throws Exception;

    TBDomaine update(Long id, TBDomaineRequestDto tbDomaineRequestDto) throws Exception;

    boolean existsById(Long id);

    void addCategorie(Long tbDomaineId, Long categorieId);

    /**
     * Vérifie si un tbdomaine appartient à un tableau de bord spécifique
     */
    boolean existsInTableauBord(Long tbDomaineId, Long tableauBordId);

    /**
     * Récupère les IDs des domaines d'un tableau de bord
     */
    List<Long> getTBDomaineIdsByTableauBordId(Long tableauBordId);

    /**
     * Returns a TBDomaine enriched with its indicateur pivot table data using the
     * provided tableFormat.
     */
    TBDomaineDetailDto getTBDomaineWithPivotTable(Long id, String tableFormat);

    /**
     * Returns a list of TBDomaines enriched with indicateur pivot table data based
     * on the provided query params.
     * Use filters in QueryParams (e.g., tableauBordDomaines.tableauBord.id) to
     * scope results.
     */
    List<TBDomaineDetailDto> getTBDomainesWithPivotTable(QueryParams requestParams, String tableFormat);

}