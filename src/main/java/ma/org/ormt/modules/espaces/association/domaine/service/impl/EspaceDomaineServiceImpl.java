package ma.org.ormt.modules.espaces.association.domaine.service.impl;

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
import ma.org.ormt.modules.espaces.association.domaine.EspaceDomaine;
import ma.org.ormt.modules.espaces.association.domaine.dtos.request.EspaceDomaineRequestDto;
import ma.org.ormt.modules.espaces.association.domaine.repository.EspaceDomaineRepository;
import ma.org.ormt.modules.espaces.association.domaine.service.EspaceDomaineService;
import ma.org.ormt.modules.espaces.models.Espace;
import ma.org.ormt.modules.espaces.repositories.EspaceRepository;

@Service
public class EspaceDomaineServiceImpl extends BaseServiceImpl<Espace> implements EspaceDomaineService {

    @Autowired
    private EspaceDomaineRepository espaceDomaineRepository;

    @Autowired
    private DomaineService domaineService;

    @Autowired
    private ObjectsValidator<EspaceDomaineRequestDto> validator;

    static final String DOMAINE_NOT_FOUND = "Domaine not found";
    static final String ESPACE_NOT_FOUND = "Espace not found";
    static final String NOT_FOUND = "attach not found";

    public EspaceDomaineServiceImpl(EspaceRepository espaceRepository, SpecificationService specificationService) {
        super(espaceRepository, specificationService);
    }

    @Transactional
    public List<EspaceDomaine> attachDomainesToEspace(List<EspaceDomaineRequestDto> espaceDomaineRequestDtos) {

        List<EspaceDomaine> espaceDomEspaceDomaines = espaceDomaineRequestDtos.stream().map(requestDto -> {

            validator.validate(requestDto);

            Espace espace = findById(requestDto.getEspace().getId())
                    .orElseThrow(() -> new EntityNotFoundException(ESPACE_NOT_FOUND));

            Domaine domaine = domaineService.findById(requestDto.getDomaine().getId())
                    .orElseThrow(() -> new EntityNotFoundException(DOMAINE_NOT_FOUND));

            EspaceDomaine espaceDomEspaceDomaine = new EspaceDomaine();
            espaceDomEspaceDomaine.setDomaine(domaine);
            espaceDomEspaceDomaine.setEspace(espace);
            return espaceDomaineRepository.save(espaceDomEspaceDomaine);
        }).toList();

        return espaceDomEspaceDomaines;
    }

    @Transactional
    public void detachDomainesFromEspace(List<Long> espaceDomaineIds) {

        espaceDomaineRepository.deleteAllById(espaceDomaineIds);

    }

}