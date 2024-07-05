package ma.org.ormt.modules.planaction.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.planaction.PlanAction;
import ma.org.ormt.modules.planaction.dto.request.PlanActionRequestDto;

public interface PlanActionService extends BaseService<PlanAction> {

    Optional<PlanAction> findByNom(String nom);

    Page<PlanAction> getEntityList(QueryParams requestParams);

    PlanAction create(PlanActionRequestDto requestDto);

    PlanAction update(Long id, PlanActionRequestDto planActionRequestDto);

    boolean existsById(Long id);

    List<String> findMissionCodesByPLanActionId(Long planActionId);

}