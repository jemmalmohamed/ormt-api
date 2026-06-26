package ma.org.ormt.modules.chiffres.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.chiffres.dtos.request.ChiffreCleRequestDto;
import ma.org.ormt.modules.chiffres.dtos.request.ChiffreCleRequestDtoMapper;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.chiffres.models.enums.KpiEvolutionMode;
import ma.org.ormt.modules.chiffres.models.enums.KpiFormatType;
import ma.org.ormt.modules.chiffres.models.enums.KpiModeSource;
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

    @Autowired
    private ObjectMapper objectMapper;

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
        updateChiffreCleFields(chiffrecleToCreate, requestDto);
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
        KpiModeSource modeSource = requestDto.getModeSource() == null ? KpiModeSource.MANUAL : requestDto.getModeSource();
        KpiFormatType formatType = requestDto.getFormatType() == null ? KpiFormatType.NUMBER : requestDto.getFormatType();
        KpiEvolutionMode evolutionMode = requestDto.getEvolutionMode() == null ? KpiEvolutionMode.NONE
                : requestDto.getEvolutionMode();

        validateBusinessRules(requestDto, modeSource);
        validateJsonPayload("styleJson", requestDto.getStyleJson());
        validateJsonPayload("metadataJson", requestDto.getMetadataJson());

        chiffrecle.setLibelle(normalize(requestDto.getLibelle()));
        chiffrecle.setUnite(normalize(requestDto.getUnite()));
        chiffrecle.setDescription(normalize(requestDto.getDescription()));
        chiffrecle.setAfficherDescription(Boolean.TRUE.equals(requestDto.getAfficherDescription()));
        chiffrecle.setAccessType(normalize(requestDto.getAccessType()));
        chiffrecle.setActif(requestDto.getActif());
        chiffrecle.setModeSource(modeSource);
        chiffrecle.setFormatType(formatType);
        chiffrecle.setPrefixLabel(normalize(requestDto.getPrefixLabel()));
        chiffrecle.setSuffixLabel(normalize(requestDto.getSuffixLabel()));
        chiffrecle.setEvolutionMode(evolutionMode);
        chiffrecle.setMetadataJson(normalizeJson(requestDto.getMetadataJson()));
        chiffrecle.setStyleJson(normalizeJson(requestDto.getStyleJson()));
        chiffrecle.setValeur(normalize(requestDto.getValeur()));

        if (modeSource == KpiModeSource.INDICATEUR_VALUE) {
            DonneeIndicateur donneeIndicateur = donneeIndicateurService
                    .findById(requestDto.getDonneeIndicateur().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Donnee Indicateur non trouvé"));
            Indicateur indicateur = indicateurService.findById(requestDto.getIndicateur().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Indicateur non trouvé"));
            chiffrecle.setDonneeIndicateur(donneeIndicateur);
            chiffrecle.setIndicateur(indicateur);
            chiffrecle.setValeur(donneeIndicateur.getValeur());
            chiffrecle.setAfficherDate(hasTemporalDimension(indicateur) && Boolean.TRUE.equals(requestDto.getAfficherDate()));
        } else {

            chiffrecle.setDonneeIndicateur(null);
            chiffrecle.setIndicateur(null);
            chiffrecle.setAfficherDate(Boolean.FALSE);

        }
    }

    private void validateBusinessRules(ChiffreCleRequestDto requestDto, KpiModeSource modeSource) {
        if (modeSource == KpiModeSource.MANUAL) {
            if (requestDto.getValeur() == null || requestDto.getValeur().isBlank()) {
                throw new IllegalArgumentException("La valeur est obligatoire pour un KPI manuel.");
            }
            return;
        }

        if (requestDto.getIndicateur() == null || requestDto.getIndicateur().getId() == null) {
            throw new IllegalArgumentException("L'indicateur est obligatoire pour un KPI issu d'un indicateur.");
        }
        if (requestDto.getDonneeIndicateur() == null || requestDto.getDonneeIndicateur().getId() == null) {
            throw new IllegalArgumentException("La donnée indicateur est obligatoire pour un KPI issu d'un indicateur.");
        }
    }

    private void validateJsonPayload(String fieldName, String payload) {
        if (payload == null || payload.isBlank()) {
            return;
        }
        try {
            objectMapper.readTree(payload);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Le champ " + fieldName + " doit contenir un JSON valide.");
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeJson(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized;
    }

    private boolean hasTemporalDimension(Indicateur indicateur) {
        return indicateur != null
                && indicateur.getIndicateurDimensions() != null
                && indicateur.getIndicateurDimensions().stream().anyMatch(dimension -> Boolean.TRUE.equals(dimension.getTemporelle()));
    }

}
