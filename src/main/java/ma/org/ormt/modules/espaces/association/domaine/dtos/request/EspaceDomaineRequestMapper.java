package ma.org.ormt.modules.espaces.association.domaine.dtos.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.espaces.association.domaine.EspaceDomaine;

@Mapper
public interface EspaceDomaineRequestMapper extends BaseDtoMapper<EspaceDomaine, EspaceDomaineRequestDto> {

}
