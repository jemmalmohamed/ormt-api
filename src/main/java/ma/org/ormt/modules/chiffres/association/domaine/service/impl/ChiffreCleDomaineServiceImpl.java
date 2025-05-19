package ma.org.ormt.modules.chiffres.association.domaine.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.services.DomaineService;
import ma.org.ormt.modules.chiffres.association.domaine.ChiffreCleDomaine;
import ma.org.ormt.modules.chiffres.association.domaine.dtos.request.ChiffreCleDomaineRequestDto;
import ma.org.ormt.modules.chiffres.association.domaine.repository.ChiffreCleDomaineRepository;
import ma.org.ormt.modules.chiffres.association.domaine.service.ChiffreCleDomaineService;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.chiffres.repositories.ChiffreCleRepository;

@Service
public class ChiffreCleDomaineServiceImpl extends BaseServiceImpl<ChiffreCle> implements ChiffreCleDomaineService {

    @Autowired
    private ChiffreCleDomaineRepository chiffrecleDomaineRepository;

    @Autowired
    private DomaineService domaineService;

    @Autowired
    private ObjectsValidator<ChiffreCleDomaineRequestDto> validator;

    static final String DOMAINE_NOT_FOUND = "Domaine not found";
    static final String CHIFFRECLE_NOT_FOUND = "ChiffreCle not found";
    static final String NOT_FOUND = "attach not found";

    public ChiffreCleDomaineServiceImpl(ChiffreCleRepository chiffrecleRepository,
            SpecificationService specificationService) {
        super(chiffrecleRepository, specificationService);
    }

    @Transactional
    public List<ChiffreCleDomaine> attachDomainesToChiffreCle(
            List<ChiffreCleDomaineRequestDto> chiffrecleDomaineRequestDtos) {

        List<ChiffreCleDomaine> chiffrecleDomChiffreCleDomaines = chiffrecleDomaineRequestDtos.stream()
                .map(requestDto -> {

                    validator.validate(requestDto);

                    ChiffreCle chiffrecle = findById(requestDto.getChiffreCle().getId())
                            .orElseThrow(() -> new EntityNotFoundException(CHIFFRECLE_NOT_FOUND));

                    Domaine domaine = domaineService.findById(requestDto.getDomaine().getId())
                            .orElseThrow(() -> new EntityNotFoundException(DOMAINE_NOT_FOUND));

                    ChiffreCleDomaine chiffrecleDomChiffreCleDomaine = new ChiffreCleDomaine();
                    chiffrecleDomChiffreCleDomaine.setDomaine(domaine);
                    chiffrecleDomChiffreCleDomaine.setChiffreCle(chiffrecle);
                    return chiffrecleDomaineRepository.save(chiffrecleDomChiffreCleDomaine);
                }).toList();

        return chiffrecleDomChiffreCleDomaines;
    }

    @Transactional
    public void detachDomainesFromChiffreCle(List<Long> chiffrecleDomaineIds) {

        chiffrecleDomaineRepository.deleteAllById(chiffrecleDomaineIds);

    }

}