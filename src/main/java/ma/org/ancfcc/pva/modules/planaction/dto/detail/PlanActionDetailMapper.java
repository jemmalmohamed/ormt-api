package ma.org.ancfcc.pva.modules.planaction.dto.detail;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseMapper;
import ma.org.ancfcc.pva.modules.planaction.PlanAction;

@Mapper
public interface PlanActionDetailMapper extends BaseMapper<PlanAction, PlanActionDetailDto> {

}
