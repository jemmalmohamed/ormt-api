package ma.org.ormt.modules.dashboard.tbgroup.dtos.request;

import org.mapstruct.Mapper;
import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.dashboard.tbgroup.models.TbGroup;

@Mapper()
public interface TbGroupRequestDtoMapper extends BaseDtoMapper<TbGroup, TbGroupRequestDto> {
}
