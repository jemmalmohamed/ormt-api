package ma.org.ancfcc.pva.modules.organisme.dto.summary;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.organisme.Organisme;

@Mapper
public interface OrganismeSummaryDtoMapper extends BaseDtoMapper<Organisme, OrganismeSummaryDto> {

}
