package ma.org.ancfcc.pva.modules.planaction.dto.summary;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.planaction.PlanAction;

@Mapper
public interface PlanActionSummaryDtoMapper extends BaseDtoMapper<PlanAction, PlanActionSummaryDto> {

}
