package ma.org.ancfcc.pva.modules.mission.service;

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
import ma.org.ancfcc.pva.modules.mission.dto.request.MissionRequestDto;
import ma.org.ancfcc.pva.modules.mission.dto.request.MissionRequestMapper;
import ma.org.ancfcc.pva.modules.mission.models.Mission;
import ma.org.ancfcc.pva.modules.mission.repository.MissionRepository;

@Service
public class MissionServiceImpl extends BaseServiceImpl<Mission> implements MissionService {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ObjectsValidator<MissionRequestDto> validator;

    @Autowired
    private MissionRequestMapper missionRequestMapper;

    static final String NOT_FOUND_STRING = "Mission not found";

    public MissionServiceImpl(MissionRepository missionRepository, SpecificationService specificationService) {
        super(missionRepository, specificationService);
    }

    @Override
    public boolean existsById(UUID id) {
        return missionRepository.existsById(id);
    }

    @Override
    public Optional<Mission> findByNom(String nom) {
        return missionRepository.findByNom(nom);
    }

    @Override
    public Optional<Mission> findByCode(String code) {
        return missionRepository.findByCode(code);
    }

    @Override
    public Page<Mission> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Mission.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Mission> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Mission.class);
        return findAll(specification, pageable);
    }

    @Override
    public Mission create(MissionRequestDto requestDto) {
        validator.validate(requestDto);
        Mission missionToCreate = missionRequestMapper.mapToEntity(requestDto);
        return missionRepository.save(missionToCreate);
    }

    @Override
    public Mission update(UUID id, MissionRequestDto requestDto) {
        // verify if id is the same as the one in the body
        validator.validate(requestDto);
        Mission missionToUpdate = missionRequestMapper.mapToEntity(requestDto);
        checkPathId(id, missionToUpdate.getId());
        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateFields(mission, missionToUpdate);
        return missionRepository.save(mission);
    }

    @Override
    public void validateBeforeDelete(UUID id) {
        validateMissionDependencies(id);
    }

    private void updateFields(Mission mission, Mission entityToUpdate) {
        mission.setNom(entityToUpdate.getNom());
        mission.setCode(entityToUpdate.getCode());
        mission.setEtat(entityToUpdate.getEtat());
        mission.setSuperficie(entityToUpdate.getSuperficie());
        mission.setDescription(entityToUpdate.getDescription());

    }

    private void validateMissionDependencies(UUID id) {
        // TODO : uncomment this code after implementing the mission module
    }

    @Override
    public Long countPhotoPlanificationsByMissionId(UUID missionId) {
        return missionRepository.countPhotoPlanificationsByMissionId(missionId);
    }

    @Override
    public Long countBandeByMissionId(UUID missionId) {
        return missionRepository.countPhotoPlanificationsByMissionId(missionId);
    }

}