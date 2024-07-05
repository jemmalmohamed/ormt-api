package ma.org.ancfcc.pva.modules.capteur.service;

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
import ma.org.ancfcc.pva.modules.capteur.Capteur;
import ma.org.ancfcc.pva.modules.capteur.dto.request.CapteurRequestDto;
import ma.org.ancfcc.pva.modules.capteur.dto.request.CapteurRequestMapper;
import ma.org.ancfcc.pva.modules.capteur.enums.CapteurCode;
import ma.org.ancfcc.pva.modules.capteur.enums.CapteurSubName;
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
    public boolean existsById(Long id) {
        return capteurRepository.existsById(id);
    }

    @Override
    public Optional<Capteur> findByNom(String nom) {
        return capteurRepository.findByNom(nom);
    }

    @Override
    public Optional<Capteur> findByCode(String code) {
        return capteurRepository.findByCode(code);
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
    public Capteur update(Long id, CapteurRequestDto requestDto) {
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
    public void validateBeforeDelete(Long id) {
        validateMissionDependencies(id);
    }

    private void updateFields(Capteur capteur, Capteur entityToUpdate) {
        capteur.setNom(entityToUpdate.getNom());
        capteur.setCode(entityToUpdate.getCode());
        capteur.setCategorie(entityToUpdate.getCategorie());
        capteur.setSerial(entityToUpdate.getSerial());
        capteur.setMode(entityToUpdate.getMode());
        capteur.setFormat(entityToUpdate.getFormat());
        capteur.setConstructeur(entityToUpdate.getConstructeur());
        capteur.setDescription(entityToUpdate.getDescription());

    }

    private void validateMissionDependencies(Long id) {
        List<String> missionList = capteurRepository.findMissionCodesByCapteurId(id);
        if (!missionList.isEmpty()) {

            String message = MessageResponse.builder()
                    .title("Suppression impossible ")
                    .mainMessage("Impossible de supprimer le capteur  car il est associé aux missions.")
                    .subMessageList(
                            missionList)
                    .build()
                    .format();

            throw new CannotDeleteException(message);
        }
    }

    @Override
    public String getCapteurCodeFromString(String nom) {
        if (nom.contains(CapteurSubName.ADS.getDescription())) {
            return CapteurCode.ADS40_80.getDescription();
        } else if (nom.contains(CapteurSubName.ALS.getDescription())) {
            return CapteurCode.ALS70.getDescription();
        } else if (nom.contains(CapteurSubName.DMC.getDescription())) {
            return CapteurCode.DMC_II_230.getDescription();
        } else if (nom.contains(CapteurSubName.RC30.getDescription())) {
            return CapteurCode.RC30.getDescription();
        } else if (nom.contains(CapteurSubName.RMK.getDescription())) {
            return CapteurCode.RMK_TOP_15.getDescription();
        } else {
            throw new EntityNotFoundException("Capteur with name '" + nom + "' not found");
        }
    }

}