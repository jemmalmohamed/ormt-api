package ma.org.ormt.modules.domaine.dto;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.domaine.Domaine;

@Mapper
public interface DomaineDtoMapper extends BaseDtoMapper<Domaine, DomaineDto> {

}
