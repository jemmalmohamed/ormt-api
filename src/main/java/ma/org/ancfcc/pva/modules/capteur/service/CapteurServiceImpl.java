package ma.org.ancfcc.pva.modules.capteur.service;

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
import ma.org.ancfcc.pva.modules.capteur.Capteur;
import ma.org.ancfcc.pva.modules.capteur.dto.request.CapteurRequestDto;
import ma.org.ancfcc.pva.modules.capteur.dto.request.CapteurRequestMapper;
import ma.org.ancfcc.pva.modules.capteur.repository.CapteurRepository;

@Service
public class CapteurServiceImpl extends BaseServiceImpl<Capteur> implements CapteurService {

    @Autowired
    private CapteurRepository capteurRepository;

    @Autowired
    private ObjectsValidator<CapteurRequestDto> validator;

    @Autowired
    private CapteurRequestMapper capteurRequestMapper;

    static final String NOT_FOUND_STRING = "Capteur not found";

    public CapteurServiceImpl(CapteurRepository capteurRepository, SpecificationService specificationService) {
        super(capteurRepository, specificationService);
    }

    @Override
    public boolean existsById(UUID id) {
        return capteurRepository.existsById(id);
    }

    @Override
    public Optional<Capteur> findByNom(String nom) {
        return capteurRepository.findByNom(nom);
    }

    @Override
    public Page<Capteur> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), Capteur.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<Capteur> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), Capteur.class);
        return findAll(specification, pageable);
    }

    @Override
    public Capteur create(CapteurRequestDto requestDto) {
        validator.validate(requestDto);
        Capteur capteurToCreate = capteurRequestMapper.mapToEntity(requestDto);
        return capteurRepository.save(capteurToCreate);
    }

    @Override
    public Capteur update(UUID id, CapteurRequestDto requestDto) {
        // verify if id is the same as the one in the body
        validator.validate(requestDto);
        Capteur capteurToUpdate = capteurRequestMapper.mapToEntity(requestDto);
        checkPathId(id, capteurToUpdate.getId());
        Capteur capteur = capteurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateFields(capteur, capteurToUpdate);
        return capteurRepository.save(capteur);
    }

    @Override
    public void validateBeforeDelete(UUID id) {
        validateMissionDependencies(id);
    }

    private void updateFields(Capteur capteur, Capteur entityToUpdate) {
        capteur.setNom(entityToUpdate.getNom());
        capteur.setCategorie(entityToUpdate.getCategorie());
        capteur.setSerial(entityToUpdate.getSerial());
        capteur.setMode(entityToUpdate.getMode());
        capteur.setFormat(entityToUpdate.getFormat());
        capteur.setConstructeur(entityToUpdate.getConstructeur());
        capteur.setDescription(entityToUpdate.getDescription());

    }

    private void validateMissionDependencies(UUID id) {
        // TODO : uncomment this code after implementing the mission module
    }

}