package ma.org.ormt.modules.organisme.dto.summary;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.organisme.Organisme;

@Mapper
public interface OrganismeSummaryDtoMapper extends BaseDtoMapper<Organisme, OrganismeSummaryDto> {

}
