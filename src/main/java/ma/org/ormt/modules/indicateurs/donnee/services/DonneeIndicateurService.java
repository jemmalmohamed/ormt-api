package ma.org.ormt.modules.indicateurs.donnee.services;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.imports.DonneeImportCommitDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.imports.DonneeImportPreviewDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.request.DonneeIndicateurRequestDto;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;

public interface DonneeIndicateurService extends BaseService<DonneeIndicateur> {

    Page<DonneeIndicateur> getEntityList(QueryParams requestParams);

    Page<DonneeIndicateur> getEntityListByIndicateurId(Long indicateurId, QueryParams requestParams);

    DonneeIndicateur create(Long indicateurId, DonneeIndicateurRequestDto requestDto);

    List<DonneeIndicateur> createBulk(Long indicateurId, List<DonneeIndicateurRequestDto> requestDtos);

    List<DonneeIndicateurDto> createByList(Long idIndicateur, List<DonneeIndicateurRequestDto> requestDto);

    DonneeIndicateur update(Long id, DonneeIndicateurRequestDto donneeInDonneeIndicateurRequestDto);

    DonneeImportPreviewDto previewImport(Long indicateurId, List<DonneeIndicateurRequestDto> requestDtos);

    DonneeImportCommitDto commitImport(
            Long indicateurId,
            List<DonneeIndicateurRequestDto> requestDtos,
            boolean overwriteConflicts,
            boolean replaceExistingData,
            Set<Integer> selectedRowNumbers);

    boolean existsById(Long id);

}
