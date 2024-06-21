package ma.org.ancfcc.pva.modules.planaction.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ancfcc.pva.core.commun.base.service.BaseServiceImpl;
import ma.org.ancfcc.pva.core.commun.base.service.SpecificationService;
import ma.org.ancfcc.pva.core.commun.rest.queries.QueryParams;
import ma.org.ancfcc.pva.core.utilities.EntityInspector;
import ma.org.ancfcc.pva.core.utilities.PaginationUtils;
import ma.org.ancfcc.pva.core.validators.ObjectsValidator;
import ma.org.ancfcc.pva.modules.planaction.PlanAction;
import ma.org.ancfcc.pva.modules.planaction.dto.request.PlanActionRequestDto;
import ma.org.ancfcc.pva.modules.planaction.dto.request.PlanActionRequestMapper;
import ma.org.ancfcc.pva.modules.planaction.repository.PlanActionRepository;

@Service
public class PlanActionServiceImpl extends BaseServiceImpl<PlanAction> implements PlanActionService {

    @Autowired
    private PlanActionRepository planActionRepository;

    @Autowired
    private ObjectsValidator<PlanActionRequestDto> validator;

    @Autowired
    private PlanActionRequestMapper planActionRequestMapper;

    public PlanActionServiceImpl(PlanActionRepository planActionRepository, SpecificationService specificationService) {
        super(planActionRepository, specificationService);
    }

    public Page<PlanAction> getPlanActions(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }

        Pageable pageable = PaginationUtils.createPageable(requestParams);

        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), PlanAction.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<PlanAction> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), PlanAction.class);
        return findAll(specification, pageable);
    }

    /**
     * @param nom
     * @return Optional<PlanAction>
     */
    @Override
    public Optional<PlanAction> findByDesignation(String designation) {
        return planActionRepository.findByDesignation(designation);
    }

    @Override
    public PlanAction update(UUID id, PlanActionRequestDto requestDto) {
        // verify if id is the same as the one in the body
        validator.validate(requestDto);
        PlanAction planActionToUpdate = planActionRequestMapper.mapToEntity(requestDto);

        checkPathId(id, planActionToUpdate.getId());
        PlanAction planAction = planActionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plan action not found"));
        updatePlanActionFields(planAction, planActionToUpdate);
        return planActionRepository.save(planAction);
    }

    private void updatePlanActionFields(PlanAction planAction, PlanAction planActionToUpdate) {
        planAction.setDescription(planActionToUpdate.getDescription());
        planAction.setDesignation(planActionToUpdate.getDesignation());
        planAction.setDebutDate(planActionToUpdate.getDebutDate());
        planAction.setFinDate(planActionToUpdate.getFinDate());
    }

    @Override
    public void validateBeforeDelete(UUID id) {
        validateMissionDependencies(id);
    }

    @Override
    public PlanAction create(PlanActionRequestDto requestDto) {
        validator.validate(requestDto);
        PlanAction planActionToCreate = planActionRequestMapper.mapToEntity(requestDto);
        return planActionRepository.save(planActionToCreate);
    }

    @Override
    public boolean existsById(UUID id) {
        return planActionRepository.existsById(id);
    }

    private void validateMissionDependencies(UUID id) {
        // TODO : uncomment this code after implementing the mission module
        // Long missionCount = missionCoreRepository.countByPlanActionId(id);
        // if (missionCount > 0) {
        // List<String> missionNames =
        // missionCoreRepository.findNamesByPlanActionId(id);
        // PlanAction planAction = planActionRepository.findById(id)
        // .orElseThrow(() -> new EntityNotFoundException("Plan action not found"));

        // String error = "Impossible de supprimer le plan d'action -" +
        // planAction.getDesignation()
        // + "- car il est associé aux missions : ";

        // String message = MessageResponse.builder()
        // .title("Suppression impossible")
        // .mainMessage(error)
        // .subMessageList(missionNames)
        // .build()
        // .format();
        // throw new CannotDeleteException(message);

        // }
    }

}