package ma.org.ormt.modules.planaction.dto.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.planaction.PlanAction;

@Mapper
public interface PlanActionRequestMapper extends BaseDtoMapper<PlanAction, PlanActionRequestDto> {

}
