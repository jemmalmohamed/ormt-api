package ma.org.ormt.modules.planaction.dto.summary;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.planaction.PlanAction;

@Mapper
public interface PlanActionSummaryDtoMapper extends BaseDtoMapper<PlanAction, PlanActionSummaryDto> {

}
