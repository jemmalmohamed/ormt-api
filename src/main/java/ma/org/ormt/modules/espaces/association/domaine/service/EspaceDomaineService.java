package ma.org.ormt.modules.espaces.association.domaine.service;

import java.util.List;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.modules.espaces.association.domaine.EspaceDomaine;
import ma.org.ormt.modules.espaces.association.domaine.dtos.request.EspaceDomaineRequestDto;
import ma.org.ormt.modules.espaces.models.Espace;

public interface EspaceDomaineService extends BaseService<Espace> {

    List<EspaceDomaine> attachDomainesToEspace(List<EspaceDomaineRequestDto> espaceDomaineRequestDto);

    void detachDomainesFromEspace(List<Long> espaceDomaineIds);

}