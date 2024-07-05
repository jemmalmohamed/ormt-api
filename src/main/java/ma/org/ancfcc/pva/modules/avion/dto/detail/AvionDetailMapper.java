package ma.org.ancfcc.pva.modules.avion.dto.detail;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.avion.Avion;

@Mapper
public interface AvionDetailMapper extends BaseDtoMapper<Avion, AvionDetailDto> {

}
