package ma.org.ormt.modules.avion.dto.detail;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.avion.Avion;

@Mapper
public interface AvionDetailMapper extends BaseDtoMapper<Avion, AvionDetailDto> {

}
