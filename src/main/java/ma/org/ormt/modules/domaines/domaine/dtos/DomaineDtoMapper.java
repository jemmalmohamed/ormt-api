package ma.org.ormt.modules.domaines.domaine.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;

@Mapper
public interface DomaineDtoMapper extends BaseDtoMapper<Domaine, DomaineDto> {

}
