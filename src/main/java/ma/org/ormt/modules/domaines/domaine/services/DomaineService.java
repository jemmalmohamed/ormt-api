package ma.org.ormt.modules.domaines.domaine.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.domaines.domaine.dtos.details.DomaineDetailDto;
import ma.org.ormt.modules.domaines.domaine.dtos.request.DomaineRequestDto;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;

public interface DomaineService extends BaseService<Domaine> {

    Optional<Domaine> findByNom(String nom);

    Page<Domaine> getEntityList(QueryParams requestParams);

    Page<Domaine> getEntitiesByIds(List<Long> ids, QueryParams requestParams);

    Domaine create(DomaineRequestDto requestDto) throws Exception;

    Domaine update(Long id, DomaineRequestDto domaineRequestDto) throws Exception;

    boolean existsById(Long id);

    void addSousDomaine(Long domaineId, Long sousDomaineId);

    /**
     * Vérifie si un domaine appartient à un espace spécifique
     */
    boolean existsInEspace(Long domaineId, Long espaceId);

    /**
     * Récupère les IDs des domaines d'un espace
     */
    List<Long> getDomaineIdsByEspaceId(Long espaceId);

    /**
     * Gets a Domaine with table data for all its indicateurs
     */
    // DomaineDetailDto getDomaineWithTableData(Long id, String tableFormat);

}