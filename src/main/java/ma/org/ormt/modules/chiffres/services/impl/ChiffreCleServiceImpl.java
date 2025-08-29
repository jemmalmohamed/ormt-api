package ma.org.ormt.modules.chiffres.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.chiffres.dtos.request.ChiffreCleRequestDto;
import ma.org.ormt.modules.chiffres.dtos.request.ChiffreCleRequestDtoMapper;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.chiffres.repositories.ChiffreCleRepository;
import ma.org.ormt.modules.chiffres.services.ChiffreCleService;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.donnee.services.DonneeIndicateurService;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@Service
@Transactional
public class ChiffreCleServiceImpl extends BaseServiceImpl<ChiffreCle> implements ChiffreCleService {

    @Autowired
    private ChiffreCleRepository chiffrecleRepository;

    @Autowired
    private DonneeIndicateurService donneeIndicateurService;
    @Autowired
    private IndicateurService indicateurService;

    @Autowired
    private ObjectsValidator<ChiffreCleRequestDto> validator;

    @Autowired
    private ChiffreCleRequestDtoMapper chiffrecleRequestMapper;

    private static final String NOT_FOUND_STRING = "ChiffreCle non trouvée";

    public ChiffreCleServiceImpl(ChiffreCleRepository chiffrecleRepository, SpecificationService specificationService) {
        super(chiffrecleRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return chiffrecleRepository.existsById(id);
    }

    @Override
    public Optional<ChiffreCle> findByLibelle(String libelle) {
        return chiffrecleRepository.findByLibelle(libelle);
    }

    @Override
    public Page<ChiffreCle> getEntityList(QueryParams requestParams) {
        return super.getEntityList(requestParams, ChiffreCle.class);
    }

    @Override
    public Page<ChiffreCle> getEntitiesByIds(List<Long> ids, QueryParams requestParams) {
        return super.getEntitiesByIds(ids, requestParams, ChiffreCle.class);
    }

    @Override
    public ChiffreCle save(ChiffreCle chiffrecle) {
        return chiffrecleRepository.save(chiffrecle);
    }

    @Override
    public ChiffreCle create(ChiffreCleRequestDto requestDto) throws Exception {

        validator.validate(requestDto);
        ChiffreCle chiffrecleToCreate = chiffrecleRequestMapper.mapToEntity(requestDto);
        ChiffreCle createdChiffreCle = chiffrecleRepository.save(chiffrecleToCreate);
        return createdChiffreCle;

    }

    @Override
    public ChiffreCle update(Long id, ChiffreCleRequestDto requestDto) throws Exception {
        validator.validate(requestDto);
        checkPathId(id, requestDto.getId());
        ChiffreCle chiffrecle = chiffrecleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateChiffreCleFields(chiffrecle, requestDto);

        return chiffrecleRepository.save(chiffrecle);
    }

    private void updateChiffreCleFields(ChiffreCle chiffrecle, ChiffreCleRequestDto requestDto) {
        chiffrecle.setLibelle(requestDto.getLibelle().toLowerCase());
        chiffrecle.setUnite(requestDto.getUnite().toLowerCase());
        chiffrecle.setDescription(requestDto.getDescription().toLowerCase());
        chiffrecle.setAfficherDate(requestDto.getAfficherDate());
        chiffrecle.setAccessType(requestDto.getAccessType());
        chiffrecle.setActif(requestDto.getActif());
        chiffrecle.setValeur(requestDto.getValeur());
        if (requestDto.getDonneeIndicateur() != null && requestDto.getIndicateur().getId() != null) {
            DonneeIndicateur donneeIndicateur = donneeIndicateurService
                    .findById(requestDto.getDonneeIndicateur().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Donnee Indicateur non trouvé"));
            Indicateur indicateur = indicateurService.findById(requestDto.getIndicateur().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Indicateur non trouvé"));
            chiffrecle.setDonneeIndicateur(donneeIndicateur);
            chiffrecle.setIndicateur(indicateur);
            chiffrecle.setValeur(donneeIndicateur.getValeur());
        } else {

            chiffrecle.setDonneeIndicateur(null);
            chiffrecle.setIndicateur(null);

        }
    }

}