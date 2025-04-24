package ma.org.ormt.modules.partenaires.partenaire.services.impl;

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
import ma.org.ormt.modules.partenaires.partenaire.dtos.request.PartenaireRequestDto;
import ma.org.ormt.modules.partenaires.partenaire.dtos.request.PartenaireRequestDtoMapper;
import ma.org.ormt.modules.partenaires.partenaire.models.Partenaire;
import ma.org.ormt.modules.partenaires.partenaire.repositories.PartenaireRepository;
import ma.org.ormt.modules.partenaires.partenaire.services.PartenaireService;

@Service
public class PartenaireServiceImpl extends BaseServiceImpl<Partenaire> implements PartenaireService {

    @Autowired
    private PartenaireRepository partenaireRepository;

    @Autowired
    private ObjectsValidator<PartenaireRequestDto> validator;

    @Autowired
    private PartenaireRequestDtoMapper partenaireRequestMapper;

    static final String NOT_FOUND_STRING = "Partenaire not found";

    public PartenaireServiceImpl(PartenaireRepository partenaireRepository, SpecificationService specificationService) {
        super(partenaireRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return partenaireRepository.existsById(id);
    }

    @Override
    public Optional<Partenaire> findByNom(String nom) {
        return partenaireRepository.findByNom(nom);
    }

    @Override
    public Page<Partenaire> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Partenaire.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Partenaire> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Partenaire.class);
        return findAll(specification, pageable);
    }

    @Override
    public Partenaire create(PartenaireRequestDto requestDto) {
        validator.validate(requestDto);
        Partenaire partenaireToCreate = partenaireRequestMapper.mapToEntity(requestDto);
        return partenaireRepository.save(partenaireToCreate);
    }

    @Override
    public Partenaire update(Long id, PartenaireRequestDto requestDto) {
        // verify if id is the same as the one in the body
        validator.validate(requestDto);
        Partenaire partenaireToUpdate = partenaireRequestMapper.mapToEntity(requestDto);
        checkPathId(id, partenaireToUpdate.getId());
        Partenaire partenaire = partenaireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateFields(partenaire, partenaireToUpdate);
        return partenaireRepository.save(partenaire);
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validatePartenaireDependencies(id);
    }

    private void updateFields(Partenaire partenaire, Partenaire entityToUpdate) {
        partenaire.setNom(entityToUpdate.getNom());
        partenaire.setDescription(entityToUpdate.getDescription());
        partenaire.setPhotoUrl(entityToUpdate.getPhotoUrl()); // Updated to use getPhotoUrl()

    }

    private void validatePartenaireDependencies(Long id) {

    }

}