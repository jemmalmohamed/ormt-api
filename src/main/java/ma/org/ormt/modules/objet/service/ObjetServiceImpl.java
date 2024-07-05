package ma.org.ormt.modules.objet.service;

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
import ma.org.ormt.modules.objet.Objet;
import ma.org.ormt.modules.objet.dto.request.ObjetRequestDto;
import ma.org.ormt.modules.objet.dto.request.ObjetRequestMapper;
import ma.org.ormt.modules.objet.repository.ObjetRepository;

@Service
public class ObjetServiceImpl extends BaseServiceImpl<Objet> implements ObjetService {

    @Autowired
    private ObjetRepository objetRepository;

    @Autowired
    private ObjectsValidator<ObjetRequestDto> validator;

    @Autowired
    private ObjetRequestMapper objetRequestMapper;

    static final String NOT_FOUND_STRING = "Objet not found";

    public ObjetServiceImpl(ObjetRepository objetRepository, SpecificationService specificationService) {
        super(objetRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return objetRepository.existsById(id);
    }

    @Override
    public Optional<Objet> findByNom(String nom) {
        return objetRepository.findByNom(nom);
    }

    @Override
    public Page<Objet> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Objet.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Objet> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Objet.class);
        return findAll(specification, pageable);
    }

    @Override
    public Objet create(ObjetRequestDto requestDto) {
        validator.validate(requestDto);
        Objet objetToCreate = objetRequestMapper.mapToEntity(requestDto);
        return objetRepository.save(objetToCreate);
    }

    @Override
    public Objet update(Long id, ObjetRequestDto requestDto) {
        // verify if id is the same as the one in the body
        validator.validate(requestDto);
        Objet objetToUpdate = objetRequestMapper.mapToEntity(requestDto);
        checkPathId(id, objetToUpdate.getId());
        Objet objet = objetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateFields(objet, objetToUpdate);
        return objetRepository.save(objet);
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validateMissionDependencies(id);
    }

    private void updateFields(Objet objet, Objet entityToUpdate) {
        objet.setNom(entityToUpdate.getNom());
        objet.setDescription(entityToUpdate.getDescription());

    }

    private void validateMissionDependencies(Long id) {
        List<String> missionList = objetRepository.findMissionCodesByObjetsId(id);
        if (!missionList.isEmpty()) {

            String message = MessageResponse.builder()
                    .title("Suppression impossible ")
                    .mainMessage("Impossible de supprimer l'objet car il est associé aux missions.")
                    .subMessageList(
                            missionList)
                    .build()
                    .format();

            throw new CannotDeleteException(message);
        }
    }

}