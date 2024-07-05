package ma.org.ormt.modules.organisme.dto;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.organisme.Organisme;

@Mapper
public interface OrganismeDtoMapper extends BaseDtoMapper<Organisme, OrganismeDto> {

}
