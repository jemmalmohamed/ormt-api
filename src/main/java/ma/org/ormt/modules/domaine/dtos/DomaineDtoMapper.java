package ma.org.ormt.modules.domaine.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.domaine.models.Domaine;

@Mapper
public interface DomaineDtoMapper extends BaseDtoMapper<Domaine, DomaineDto> {

}
