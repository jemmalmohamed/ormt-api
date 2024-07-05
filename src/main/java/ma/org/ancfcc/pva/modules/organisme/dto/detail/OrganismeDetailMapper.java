package ma.org.ancfcc.pva.modules.organisme.dto.detail;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.organisme.Organisme;

@Mapper
public interface OrganismeDetailMapper extends BaseDtoMapper<Organisme, OrganismeDetailDto> {

}
