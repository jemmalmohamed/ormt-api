package ma.org.ancfcc.pva.modules.avion.dto.request;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.avion.Avion;

@Mapper
public interface AvionRequestMapper extends BaseDtoMapper<Avion, AvionRequestDto> {

}
