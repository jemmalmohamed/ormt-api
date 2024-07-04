package ma.org.ancfcc.pva.modules.avion.service;

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
import ma.org.ancfcc.pva.modules.avion.Avion;
import ma.org.ancfcc.pva.modules.avion.dto.request.AvionRequestDto;
import ma.org.ancfcc.pva.modules.avion.dto.request.AvionRequestMapper;
import ma.org.ancfcc.pva.modules.avion.repository.AvionRepository;

@Service
public class AvionServiceImpl extends BaseServiceImpl<Avion> implements AvionService {

    @Autowired
    private AvionRepository avionRepository;

    @Autowired
    private ObjectsValidator<AvionRequestDto> validator;

    @Autowired
    private AvionRequestMapper avionRequestMapper;

    static final String NOT_FOUND_STRING = "Avion not found";

    public AvionServiceImpl(AvionRepository avionRepository, SpecificationService specificationService) {
        super(avionRepository, specificationService);
    }

    @Override
    public boolean existsById(UUID id) {
        return avionRepository.existsById(id);
    }

    @Override
    public Optional<Avion> findByMatricule(String matricule) {
        return avionRepository.findByMatricule(matricule);
    }

    @Override
    public Page<Avion> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Avion.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Avion> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Avion.class);
        return findAll(specification, pageable);
    }

    @Override
    public Avion create(AvionRequestDto requestDto) {
        validator.validate(requestDto);
        Avion avionToCreate = avionRequestMapper.mapToEntity(requestDto);
        return avionRepository.save(avionToCreate);
    }

    @Override
    public Avion update(UUID id, AvionRequestDto requestDto) {
        // verify if id is the same as the one in the body
        validator.validate(requestDto);
        Avion avionToUpdate = avionRequestMapper.mapToEntity(requestDto);
        checkPathId(id, avionToUpdate.getId());
        Avion avion = avionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateFields(avion, avionToUpdate);
        return avionRepository.save(avion);
    }

    @Override
    public void validateBeforeDelete(UUID id) {
        validateCapteurDependencies();
    }

    private void updateFields(Avion avion, Avion entityToUpdate) {
        avion.setMatricule(entityToUpdate.getMatricule());
        avion.setMarque(entityToUpdate.getMarque());
        avion.setModele(entityToUpdate.getModele());

    }

    private void validateCapteurDependencies() {
        // TODO : uncomment this code after implementing the mission module
    }

}