package ma.org.ancfcc.pva.modules.organisme.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ancfcc.pva.core.commun.base.service.BaseServiceImpl;
import ma.org.ancfcc.pva.core.commun.base.service.SpecificationService;
import ma.org.ancfcc.pva.core.commun.rest.queries.QueryParams;
import ma.org.ancfcc.pva.core.commun.rest.responses.MessageResponse;
import ma.org.ancfcc.pva.core.exceptions.handlers.CannotDeleteException;
import ma.org.ancfcc.pva.core.utilities.EntityInspector;
import ma.org.ancfcc.pva.core.utilities.PaginationUtils;
import ma.org.ancfcc.pva.core.validators.ObjectsValidator;
import ma.org.ancfcc.pva.modules.organisme.Organisme;
import ma.org.ancfcc.pva.modules.organisme.dto.request.OrganismeRequestDto;
import ma.org.ancfcc.pva.modules.organisme.dto.request.OrganismeRequestMapper;
import ma.org.ancfcc.pva.modules.organisme.repository.OrganismeRepository;

@Service
public class OrganismeServiceImpl extends BaseServiceImpl<Organisme> implements OrganismeService {

    @Autowired
    private OrganismeRepository organismeRepository;

    @Autowired
    private ObjectsValidator<OrganismeRequestDto> validator;

    @Autowired
    private OrganismeRequestMapper organismeRequestMapper;

    static final String NOT_FOUND_STRING = "Organisme not found";

    public OrganismeServiceImpl(OrganismeRepository organismeRepository, SpecificationService specificationService) {
        super(organismeRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return organismeRepository.existsById(id);
    }

    @Override
    public Optional<Organisme> findByNom(String nom) {
        return organismeRepository.findByNom(nom);
    }

    @Override
    public Page<Organisme> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Organisme.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Organisme> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Organisme.class);
        return findAll(specification, pageable);
    }

    @Override
    public Organisme create(OrganismeRequestDto requestDto) {
        validator.validate(requestDto);
        Organisme organismeToCreate = organismeRequestMapper.mapToEntity(requestDto);
        return organismeRepository.save(organismeToCreate);
    }

    @Override
    public Organisme update(Long id, OrganismeRequestDto requestDto) {
        // verify if id is the same as the one in the body
        validator.validate(requestDto);
        Organisme organismeToUpdate = organismeRequestMapper.mapToEntity(requestDto);
        checkPathId(id, organismeToUpdate.getId());
        Organisme organisme = organismeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateFields(organisme, organismeToUpdate);
        return organismeRepository.save(organisme);
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validateMissionDependencies(id);
    }

    private void updateFields(Organisme organisme, Organisme entityToUpdate) {
        organisme.setNom(entityToUpdate.getNom());
        organisme.setSecteur(entityToUpdate.getSecteur());

    }

    private void validateMissionDependencies(Long id) {
        List<String> missionList = findMissionCodesByOrganismeId(id);
        if (!missionList.isEmpty()) {

            String message = MessageResponse.builder()
                    .title("Suppression impossible ")
                    .mainMessage("Impossible de supprimer l'organisme  car il est associé aux missions.")
                    .subMessageList(
                            missionList)
                    .build()
                    .format();

            throw new CannotDeleteException(message);
        }
    }

    public List<String> findMissionCodesByOrganismeId(Long organismeId) {
        return organismeRepository.findMissionCodesByOrganismeId(organismeId);
    }

}