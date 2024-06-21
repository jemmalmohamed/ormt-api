package ma.org.ancfcc.pva.modules.planaction.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ancfcc.pva.core.commun.base.service.BaseService;
import ma.org.ancfcc.pva.core.commun.rest.queries.QueryParams;
import ma.org.ancfcc.pva.modules.planaction.PlanAction;
import ma.org.ancfcc.pva.modules.planaction.dto.request.PlanActionRequestDto;

public interface PlanActionService extends BaseService<PlanAction, Long> {

    Optional<PlanAction> findByDesignation(String designation);

    Page<PlanAction> getPlanActions(QueryParams requestParams);

    PlanAction create(PlanActionRequestDto requestDto);

    PlanAction update(Long id, PlanActionRequestDto planActionRequestDto);

    boolean existsById(Long id);

}