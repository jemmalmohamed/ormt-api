package ma.org.ormt.modules.indicateurs.donnee.services;

import java.util.List;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.request.DonneeIndicateurRequestDto;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;

public interface DonneeIndicateurService extends BaseService<DonneeIndicateur> {

    // Page<DonneeIndicateur> getEntityList(QueryParams requestParams);

    List<DonneeIndicateurDto> create(Long idIndicateur, List<DonneeIndicateurRequestDto> requestDto);

    DonneeIndicateur update(Long id, DonneeIndicateurRequestDto donneeIndicateurRequestDto);

    DonneeIndicateur save(DonneeIndicateur donneeIndicateur);

    boolean existsById(Long id);

}