package ma.org.ormt.modules.gis.province.dto.detail;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.gis.province.Province;

@Mapper
public interface ProvinceDetailMapper extends BaseDtoMapper<Province, ProvinceDetailDto> {

}
