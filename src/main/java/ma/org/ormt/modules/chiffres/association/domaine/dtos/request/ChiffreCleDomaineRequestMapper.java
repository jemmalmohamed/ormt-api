package ma.org.ormt.modules.chiffres.association.domaine.dtos.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.chiffres.association.domaine.ChiffreCleDomaine;

@Mapper
public interface ChiffreCleDomaineRequestMapper extends BaseDtoMapper<ChiffreCleDomaine, ChiffreCleDomaineRequestDto> {

}
