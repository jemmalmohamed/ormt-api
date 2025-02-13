package ma.org.ormt.modules.domaine.service;

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
import ma.org.ormt.core.utilities.EntityInspector;
import ma.org.ormt.core.utilities.PaginationUtils;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.domaine.Domaine;
import ma.org.ormt.modules.domaine.dto.request.DomaineRequestDto;
import ma.org.ormt.modules.domaine.dto.request.DomaineRequestMapper;
import ma.org.ormt.modules.domaine.repository.DomaineRepository;

@Service
public class DomaineServiceImpl extends BaseServiceImpl<Domaine> implements DomaineService {

    @Autowired
    private DomaineRepository domaineRepository;

    @Autowired
    private ObjectsValidator<DomaineRequestDto> validator;

    @Autowired
    private DomaineRequestMapper domaineRequestMapper;

    static final String NOT_FOUND_STRING = "Domaine not found";

    public DomaineServiceImpl(DomaineRepository domaineRepository, SpecificationService specificationService) {
        super(domaineRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return domaineRepository.existsById(id);
    }

    @Override
    public Optional<Domaine> findByTitre(String titre) {
        return domaineRepository.findByTitre(titre);
    }

    @Override
    public Page<Domaine> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Domaine.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Domaine> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Domaine.class);
        return findAll(specification, pageable);
    }

    @Override
    public Domaine create(DomaineRequestDto requestDto) {
        validator.validate(requestDto);
        Domaine domaineToCreate = domaineRequestMapper.mapToEntity(requestDto);
        return domaineRepository.save(domaineToCreate);
    }

    @Override
    public Domaine update(Long id, DomaineRequestDto requestDto) {
        // verify if id is the same as the one in the body
        validator.validate(requestDto);
        Domaine domaineToUpdate = domaineRequestMapper.mapToEntity(requestDto);
        checkPathId(id, domaineToUpdate.getId());
        Domaine domaine = domaineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateFields(domaine, domaineToUpdate);
        return domaineRepository.save(domaine);
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validateDomaineDependencies(id);
    }

    private void updateFields(Domaine domaine, Domaine entityToUpdate) {
        domaine.setTitre(entityToUpdate.getTitre());
        domaine.setDescription(entityToUpdate.getDescription());

    }

    private void validateDomaineDependencies(Long id) {

    }

}