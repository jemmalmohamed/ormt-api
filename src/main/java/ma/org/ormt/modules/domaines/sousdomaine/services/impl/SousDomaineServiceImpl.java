package ma.org.ormt.modules.domaines.sousdomaine.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.base.specification.SpecificationAndPageable;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.services.DomaineService;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.request.SousDomaineRequestDto;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.request.SousDomaineRequestDtoMapper;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.domaines.sousdomaine.repositories.SousDomaineRepository;
import ma.org.ormt.modules.domaines.sousdomaine.services.SousDomaineService;

@Service
public class SousDomaineServiceImpl extends BaseServiceImpl<SousDomaine> implements SousDomaineService {

    @Autowired
    private SousDomaineRepository sousDomaineRepository;

    @Autowired
    private DomaineService domaineService;

    @Autowired
    private ObjectsValidator<SousDomaineRequestDto> validator;

    @Autowired
    private SousDomaineRequestDtoMapper sousDomaineRequestMapper;

    static final String NOT_FOUND_STRING = "SousDomaine not found";
    static final String DOMAINE_NOT_FOUND = "Domaine not found";

    public SousDomaineServiceImpl(SousDomaineRepository sousDomaineRepository,
            SpecificationService specificationService) {
        super(sousDomaineRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return sousDomaineRepository.existsById(id);
    }

    @Override
    public Optional<SousDomaine> findByNom(String nom) {
        return sousDomaineRepository.findByNom(nom);
    }

    @Override
    public Page<SousDomaine> getEntityList(QueryParams requestParams) {

        SpecificationAndPageable<SousDomaine> result = getSpecificationAndPageable(requestParams, SousDomaine.class);

        return findAll(result.getSpecification(), result.getPageable());
    }

    @Override
    public Page<SousDomaine> getEntityListByDomaineId(Long domaineId, QueryParams requestParams) {

        SpecificationAndPageable<SousDomaine> result = getSpecificationAndPageable(requestParams, SousDomaine.class);

        Specification<SousDomaine> domaineSpec = (root, _, criteriaBuilder) -> {
            Predicate domainePredicate = criteriaBuilder.equal(root.get("domaine").get("id"), domaineId);
            return domainePredicate;
        };

        Specification<SousDomaine> combinedSpec = addPredicateToSpecification(result.getSpecification(), domaineSpec);

        return findAll(combinedSpec, result.getPageable());
    }

    @Override
    public SousDomaine create(Long domaineId, SousDomaineRequestDto requestDto) {
        validator.validate(requestDto);
        Domaine domaine = domaineService.findById(
                domaineId)
                .orElseThrow(() -> new EntityNotFoundException(DOMAINE_NOT_FOUND));

        SousDomaine sousDomaineToCreate = sousDomaineRequestMapper.mapToEntity(requestDto);
        sousDomaineToCreate.setDomaine(domaine);
        return sousDomaineRepository.save(sousDomaineToCreate);
    }

    @Override
    public SousDomaine update(Long id, SousDomaineRequestDto requestDto) {
        validator.validate(requestDto);
        SousDomaine sousDomaineToUpdate = sousDomaineRequestMapper.mapToEntity(requestDto);
        checkPathId(id, sousDomaineToUpdate.getId());

        SousDomaine sousDomaine = sousDomaineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));

        updateFields(sousDomaine, sousDomaineToUpdate);

        return sousDomaineRepository.save(sousDomaine);
    }

    @Override
    public void validateBeforeDelete(Long id) {
        validateSousDomaineDependencies(id);
    }

    private void updateFields(SousDomaine sousDomaine, SousDomaine entityToUpdate) {
        sousDomaine.setNom(entityToUpdate.getNom());
        sousDomaine.setDescription(entityToUpdate.getDescription());
    }

    private void validateSousDomaineDependencies(Long id) {

    }

}