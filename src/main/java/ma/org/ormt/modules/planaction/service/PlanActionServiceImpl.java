package ma.org.ormt.modules.planaction.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.commun.rest.responses.MessageResponse;
import ma.org.ormt.core.exceptions.handlers.CannotDeleteException;
import ma.org.ormt.core.utilities.EntityInspector;
import ma.org.ormt.core.utilities.PaginationUtils;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.planaction.PlanAction;
import ma.org.ormt.modules.planaction.dto.request.PlanActionRequestDto;
import ma.org.ormt.modules.planaction.dto.request.PlanActionRequestMapper;
import ma.org.ormt.modules.planaction.repository.PlanActionRepository;

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

    public Page<PlanAction> getEntityList(QueryParams requestParams) {

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
    public Optional<PlanAction> findByNom(String nom) {
        return planActionRepository.findByNom(nom);
    }

    @Override
    public PlanAction update(Long id, PlanActionRequestDto requestDto) {
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
        planAction.setNom(planActionToUpdate.getNom());
        planAction.setDebutDate(planActionToUpdate.getDebutDate());
        planAction.setFinDate(planActionToUpdate.getFinDate());
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validateMissionDependencies(id);
    }

    @Override
    public PlanAction create(PlanActionRequestDto requestDto) {
        validator.validate(requestDto);
        PlanAction planActionToCreate = planActionRequestMapper.mapToEntity(requestDto);
        return planActionRepository.save(planActionToCreate);
    }

    @Override
    public boolean existsById(Long id) {
        return planActionRepository.existsById(id);
    }

    private void validateMissionDependencies(Long id) {
        List<String> missionList = findMissionCodesByPLanActionId(id);
        if (!missionList.isEmpty()) {

            String message = MessageResponse.builder()
                    .title("Suppression impossible ")
                    .mainMessage("Impossible de supprimer le plan d'action  car il est associé aux missions.")
                    .subMessageList(
                            missionList)
                    .build()
                    .format();

            throw new CannotDeleteException(message);
        }
    }

    @Override
    public List<String> findMissionCodesByPLanActionId(Long planActionId) {
        return planActionRepository.findMissionCodesByPLanActionId(planActionId);
    }

}