package ma.org.ormt.modules.chiffres.association.domaine.service;

import java.util.List;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.modules.chiffres.association.domaine.ChiffreCleDomaine;
import ma.org.ormt.modules.chiffres.association.domaine.dtos.request.ChiffreCleDomaineRequestDto;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;

public interface ChiffreCleDomaineService extends BaseService<ChiffreCle> {

    List<ChiffreCleDomaine> attachDomainesToChiffreCle(List<ChiffreCleDomaineRequestDto> chiffrecleDomaineRequestDto);

    void detachDomainesFromChiffreCle(List<Long> chiffrecleDomaineIds);

}