package ma.org.ormt.modules.espaces.association.domaine.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.espaces.association.domaine.EspaceDomaine;

@Mapper
public interface EspaceToDomaineDtoMapper extends BaseDtoMapper<EspaceDomaine, EspaceToDomaineDto> {
}