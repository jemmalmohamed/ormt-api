package ma.org.ormt.modules.domaines.sousdomaine.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.request.SousDomaineRequestDto;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.SousDomaineDto;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.details.SousDomaineDetailsDto;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;

public interface SousDomaineService extends BaseService<SousDomaine> {

    Optional<SousDomaine> findByNom(String nom);

    Page<SousDomaine> getEntityList(QueryParams requestParams);

    Page<SousDomaine> getEntityListByDomaineId(Long domaineId, QueryParams requestParams);

    SousDomaine create(Long domaineId, SousDomaineRequestDto requestDto);

    SousDomaine update(Long id, SousDomaineRequestDto sousDomaineRequestDto);

    boolean existsById(Long id);

    SousDomaine associateIndicateurToSousDomaine(Long sousDomaineId, List<Long> indicateurIds);

    SousDomaine dissociateIndicateurFromSousDomaine(Long sousDomaineId, List<Long> indicateurIds);

    // New method to get SousDomaine with table data for indicateurs
    SousDomaineDto getSousDomaineWithIndicateurTableData(Long id, String tableFormat);

    // New method to get SousDomaineDetailsDto with pivot table data
    SousDomaineDetailsDto getSousDomaineWithPivotTable(Long id, String tableFormat);

    // New method to get List of SousDomaineDetailsDto with pivot table data
    List<SousDomaineDetailsDto> getSousDomainesWithPivotTable(Long domaineId, QueryParams requestParams,
            String tableFormat);

}