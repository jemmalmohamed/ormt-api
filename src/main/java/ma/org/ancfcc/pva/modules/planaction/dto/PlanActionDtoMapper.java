package ma.org.ancfcc.pva.modules.planaction.dto;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.planaction.PlanAction;

@Mapper
public interface PlanActionDtoMapper extends BaseDtoMapper<PlanAction, PlanActionDto> {

}
