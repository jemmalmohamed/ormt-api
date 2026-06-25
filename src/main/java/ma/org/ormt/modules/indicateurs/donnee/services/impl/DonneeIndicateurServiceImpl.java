package ma.org.ormt.modules.indicateurs.donnee.services.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.base.specification.SpecificationAndPageable;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.dimension.services.DimensionService;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDtoMapper;
import ma.org.ormt.modules.indicateurs.donnee.dtos.imports.DonneeImportCommitDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.imports.DonneeImportConflictDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.imports.DonneeImportDiagnosedRowDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.imports.DonneeImportPreviewDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.imports.DonneeImportRowIssueDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.details.DonneeIndicateurDetailsDtoMapper;
import ma.org.ormt.modules.indicateurs.donnee.dtos.request.DonneeIndicateurRequestDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.request.DonneeIndicateurRequestDtoMapper;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.donnee.repositories.DonneeIndicateurRepository;
import ma.org.ormt.modules.indicateurs.donnee.services.DonneeIndicateurService;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;
import ma.org.ormt.modules.indicateurs.valeurdimension.dtos.request.ValeurDimensionRequestDto;
import ma.org.ormt.modules.indicateurs.valeurdimension.models.ValeurDimension;
import ma.org.ormt.modules.indicateurs.valeurdimension.repositories.ValeurDimensionRepository;

@Service
@Transactional
public class DonneeIndicateurServiceImpl extends BaseServiceImpl<DonneeIndicateur> implements DonneeIndicateurService {

    private static final Logger logger = LoggerFactory.getLogger(DonneeIndicateurServiceImpl.class);
    private static final String NOT_FOUND_STRING = "DonneeIndicateur not found with ID: ";
    private static final String INDICATEUR_NOT_FOUND = "Indicateur not found with ID: ";
    private static final String DIMENSION_NOT_FOUND = "Dimension not found with name: ";
    private static final String ERROR_CREATING = "Erreur lors de la création de la donneeIndicateur: ";

    private final DonneeIndicateurRepository donneeIndicateurRepository;
    private final IndicateurService indicateurService;
    private final ValeurDimensionRepository valeurDimensionRepository;
    private final DonneeIndicateurDtoMapper donneeIndicateurDtoMapper;
    private final DimensionService dimensionService;
    private final ObjectsValidator<DonneeIndicateurRequestDto> validator;
    private final DonneeIndicateurRequestDtoMapper donneeIndicateurRequestMapper;

    @Autowired
    public DonneeIndicateurServiceImpl(
            DonneeIndicateurRepository donneeIndicateurRepository,
            SpecificationService specificationService,
            IndicateurService indicateurService,
            ValeurDimensionRepository valeurDimensionRepository,
            DonneeIndicateurDtoMapper donneeIndicateurDtoMapper,
            DonneeIndicateurDetailsDtoMapper donneeIndicateurDetailMapper,
            DimensionService dimensionService,
            ObjectsValidator<DonneeIndicateurRequestDto> validator,
            DonneeIndicateurRequestDtoMapper donneeIndicateurRequestMapper) {
        super(donneeIndicateurRepository, specificationService);
        this.donneeIndicateurRepository = donneeIndicateurRepository;
        this.indicateurService = indicateurService;
        this.valeurDimensionRepository = valeurDimensionRepository;
        this.donneeIndicateurDtoMapper = donneeIndicateurDtoMapper;
        this.dimensionService = dimensionService;
        this.validator = validator;
        this.donneeIndicateurRequestMapper = donneeIndicateurRequestMapper;
    }

    @Override
    public boolean existsById(Long id) {
        return donneeIndicateurRepository.existsById(id);
    }

    @Override
    public Page<DonneeIndicateur> getEntityList(QueryParams requestParams) {
        SpecificationAndPageable<DonneeIndicateur> result = getSpecificationAndPageable(requestParams,
                DonneeIndicateur.class);
        return findAll(result.getSpecification(), result.getPageable());
    }

    @Override
    public Page<DonneeIndicateur> getEntityListByIndicateurId(Long indicateurId, QueryParams requestParams) {
        SpecificationAndPageable<DonneeIndicateur> result = getSpecificationAndPageable(requestParams,
                DonneeIndicateur.class);

        Specification<DonneeIndicateur> indicateurSpec = (root, _, criteriaBuilder) -> criteriaBuilder
                .equal(root.get("indicateur").get("id"), indicateurId);

        Specification<DonneeIndicateur> combinedSpec = addPredicateToSpecification(result.getSpecification(),
                indicateurSpec);

        return findAll(combinedSpec, result.getPageable());
    }

    public List<DonneeIndicateur> createBulk(Long indicateurId, List<DonneeIndicateurRequestDto> requestDtos) {
        return requestDtos.stream()
                .map(dto -> create(indicateurId, dto))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DonneeImportPreviewDto previewImport(Long indicateurId, List<DonneeIndicateurRequestDto> requestDtos) {
        ImportAnalysis analysis = analyzeImport(indicateurId, requestDtos);
        return analysis.previewDto;
    }

    @Override
    @Transactional
    public DonneeImportCommitDto commitImport(Long indicateurId, List<DonneeIndicateurRequestDto> requestDtos,
            boolean overwriteConflicts, boolean replaceExistingData, Set<Integer> selectedRowNumbers) {
        ImportAnalysis analysis = analyzeImport(indicateurId, requestDtos);
        DonneeImportCommitDto result = new DonneeImportCommitDto();
        copyPreview(result, analysis.previewDto);

        int beforeCount = donneeIndicateurRepository.findAllByIndicateurId(indicateurId).size();
        int importedRows = 0;
        int overwrittenRows = 0;
        Set<Integer> effectiveSelection = selectedRowNumbers == null ? Set.of() : selectedRowNumbers;
        boolean hasSelection = !effectiveSelection.isEmpty();

        List<ValidatedImportRow> selectedNewRows = analysis.newRows.stream()
                .filter(row -> !hasSelection || effectiveSelection.contains(row.rowNumber()))
                .toList();
        List<ConflictImportRow> selectedConflictRows = analysis.conflictRows.stream()
                .filter(row -> !hasSelection || effectiveSelection.contains(row.importRow().rowNumber()))
                .toList();

        if (replaceExistingData) {
            valeurDimensionRepository.deleteAllByIndicateurId(indicateurId);
            donneeIndicateurRepository.deleteAllByIndicateurId(indicateurId);

            Map<Integer, DonneeImportDiagnosedRowDto> diagnosticsByRowNumber = analysis.previewDto.getDiagnosticRows().stream()
                    .collect(Collectors.toMap(DonneeImportDiagnosedRowDto::getRowNumber, Function.identity()));

            for (int index = 0; index < requestDtos.size(); index++) {
                int rowNumber = index + 1;
                if (hasSelection && !effectiveSelection.contains(rowNumber)) {
                    continue;
                }

                DonneeImportDiagnosedRowDto diagnostic = diagnosticsByRowNumber.get(rowNumber);
                if (diagnostic == null || "rejected".equals(diagnostic.getStatus())) {
                    continue;
                }

                if ("duplicate".equals(diagnostic.getStatus()) && diagnostic.getExistingDonneeId() == null) {
                    continue;
                }

                create(indicateurId, requestDtos.get(index));
                importedRows++;
            }
        } else {
            for (ValidatedImportRow row : selectedNewRows) {
                create(indicateurId, row.requestDto());
                importedRows++;
            }

            if (overwriteConflicts) {
                for (ConflictImportRow conflict : selectedConflictRows) {
                    conflict.existingDonnee()
                            .setValeur(normalizeStoredValeur(conflict.importRow().requestDto().getValeur()));
                    donneeIndicateurRepository.save(conflict.existingDonnee());
                    overwrittenRows++;
                }
            }
        }

        int afterCount = donneeIndicateurRepository.findAllByIndicateurId(indicateurId).size();

        result.setImportedRows(importedRows);
        result.setOverwrittenRows(overwrittenRows);
        result.setSkippedConflictRows(replaceExistingData || overwriteConflicts ? 0 : selectedConflictRows.size());
        result.setBeforeCount(beforeCount);
        result.setAfterCount(afterCount);

        return result;
    }

    @Override
    @Transactional
    public DonneeIndicateur create(Long indicateurId, DonneeIndicateurRequestDto requestDto) {
        try {
            validator.validate(requestDto);

            // Get indicateur
            Indicateur indicateur = getIndicateurById(indicateurId);

            // Create and save DonneeIndicateur
            DonneeIndicateur donneeIndicateur = donneeIndicateurRequestMapper.mapToEntity(requestDto);
            donneeIndicateur.setIndicateur(indicateur);
            donneeIndicateurRepository.save(donneeIndicateur);

            // Process and save ValeurDimensions
            processValeurDimensions(requestDto.getValeurDimensions(), donneeIndicateur);

            return donneeIndicateur;
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during create operation", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error during create operation", e);
            throw new IllegalArgumentException(ERROR_CREATING + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public List<DonneeIndicateurDto> createByList(Long indicateurId, List<DonneeIndicateurRequestDto> requestDtos) {
        try {
            List<DonneeIndicateur> createdEntities = new ArrayList<>();

            // Get indicateur once for all entities
            Indicateur indicateur = getIndicateurById(indicateurId);

            for (DonneeIndicateurRequestDto dto : requestDtos) {
                validator.validate(dto);

                // Create and save DonneeIndicateur
                DonneeIndicateur donneeIndicateur = donneeIndicateurRequestMapper.mapToEntity(dto);
                donneeIndicateur.setIndicateur(indicateur);
                donneeIndicateurRepository.save(donneeIndicateur);

                // Process and save ValeurDimensions
                processValeurDimensions(dto.getValeurDimensions(), donneeIndicateur);

                createdEntities.add(donneeIndicateur);
            }

            // Map to DTOs
            return createdEntities.stream()
                    .map(donneeIndicateurDtoMapper::mapToDto)
                    .collect(Collectors.toList());
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during bulk create operation", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error during bulk create operation", e);
            throw new IllegalArgumentException(ERROR_CREATING + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public DonneeIndicateur update(Long id, DonneeIndicateurRequestDto requestDto) {
        try {
            validator.validate(requestDto);

            // Check if entity exists
            DonneeIndicateur existingDonnee = donneeIndicateurRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING + id));

            // Update base fields
            existingDonnee.setValeur(requestDto.getValeur());

            // Process ValeurDimensions if present
            if (requestDto.getValeurDimensions() != null) {
                // Remove existing ValeurDimension entities
                if (!CollectionUtils.isEmpty(existingDonnee.getValeurDimensions())) {
                    existingDonnee.getValeurDimensions()
                            .forEach(vd -> valeurDimensionRepository.deleteById(vd.getId()));
                    existingDonnee.getValeurDimensions().clear();
                }

                // Add new ValeurDimension entities
                processValeurDimensions(requestDto.getValeurDimensions(), existingDonnee);
            }

            return donneeIndicateurRepository.save(existingDonnee);
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during update operation", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error during update operation: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Error updating DonneeIndicateur: " + e.getMessage(), e);
        }
    }

    @Override
    public void validateBeforeDelete(Long id) {
        // Verify the entity exists
        donneeIndicateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING + id));

        // Add any additional validation logic here
    }

    /**
     * Helper method to get an Indicateur by ID
     */
    private Indicateur getIndicateurById(Long indicateurId) {
        return indicateurService.findById(indicateurId)
                .orElseThrow(() -> new EntityNotFoundException(INDICATEUR_NOT_FOUND + indicateurId));
    }

    /**
     * Helper method to process and save ValeurDimension entities
     */
    private void processValeurDimensions(List<ValeurDimensionRequestDto> valeurDimensionsDto,
            DonneeIndicateur donneeIndicateur) {
        if (CollectionUtils.isEmpty(valeurDimensionsDto)) {
            return;
        }

        List<ValeurDimension> dimensions = valeurDimensionsDto.stream().map(vdDto -> {
            String dimensionName = vdDto.getDimension().getNom();
            Dimension dimension = dimensionService.findByNom(dimensionName)
                    .orElseThrow(() -> new EntityNotFoundException(DIMENSION_NOT_FOUND + dimensionName));

            ValeurDimension valeurDimension = new ValeurDimension();
            valeurDimension.setDimension(dimension);
            valeurDimension.setValeur(normalizeStoredDimensionValue(vdDto.getValeur()));
            valeurDimension.setDonneeIndicateur(donneeIndicateur);
            return valeurDimension;
        }).collect(Collectors.toList());

        // Save all entities in batch
        dimensions.forEach(valeurDimensionRepository::save);

        // Update the relationship
        donneeIndicateur.setValeurDimensions(dimensions);
    }

    private ImportAnalysis analyzeImport(Long indicateurId, List<DonneeIndicateurRequestDto> requestDtos) {
        Indicateur indicateur = getIndicateurById(indicateurId);
        List<DonneeIndicateur> existingRows = donneeIndicateurRepository.findAllByIndicateurId(indicateurId);
        Set<Long> allowedDimensionIds = indicateur.getIndicateurDimensions() == null
                ? Set.of()
                : indicateur.getIndicateurDimensions().stream()
                        .map(indicateurDimension -> indicateurDimension.getDimension().getId())
                        .collect(Collectors.toSet());

        Map<String, ExistingRowSnapshot> existingByKey = existingRows.stream()
                .map(this::toExistingRowSnapshot)
                .filter(snapshot -> snapshot.dimensionKey != null)
                .collect(Collectors.toMap(
                        snapshot -> snapshot.dimensionKey,
                        snapshot -> snapshot,
                        (left, _) -> left,
                        HashMap::new));

        Map<String, ValidatedImportRow> stagedByKey = new HashMap<>();
        ImportAnalysis analysis = new ImportAnalysis();
        analysis.previewDto.setRowsReceived(requestDtos == null ? 0 : requestDtos.size());

        if (requestDtos == null || requestDtos.isEmpty()) {
            analysis.previewDto.setHasBlockingErrors(true);
            return analysis;
        }

        Set<Long> mappedDimensionIds = new HashSet<>();
        Double minValue = null;
        Double maxValue = null;

        for (int index = 0; index < requestDtos.size(); index++) {
            int rowNumber = index + 1;
            DonneeIndicateurRequestDto requestDto = requestDtos.get(index);
            ValidationResult validation = validateImportRow(requestDto, rowNumber, allowedDimensionIds);

            if (!validation.isValid()) {
                analysis.previewDto.getRejected()
                        .add(new DonneeImportRowIssueDto(rowNumber, validation.reason, validation.dimensionsLabel,
                                requestDto != null ? requestDto.getValeur() : null));
                analysis.previewDto.getDiagnosticRows()
                        .add(buildDiagnosticRow(rowNumber, "rejected", validation.reason, validation.dimensionsLabel,
                                requestDto != null ? requestDto.getValeur() : null, null, null,
                                requestDto != null ? requestDto.getValeur() : null));
                if (validation.missingValue) {
                    analysis.previewDto.setMissingValueRows(analysis.previewDto.getMissingValueRows() + 1);
                }
                if (validation.missingDimensions) {
                    analysis.previewDto.setMissingDimensionsRows(analysis.previewDto.getMissingDimensionsRows() + 1);
                }
                continue;
            }

            mappedDimensionIds.addAll(validation.dimensionIds);
            minValue = updateMin(minValue, validation.numericValue);
            maxValue = updateMax(maxValue, validation.numericValue);

            ValidatedImportRow currentRow = new ValidatedImportRow(rowNumber, requestDto, validation.dimensionKey,
                    validation.dimensionsLabel, validation.normalizedValue);

            ValidatedImportRow stagedRow = stagedByKey.get(validation.dimensionKey);
            if (stagedRow != null) {
                if (stagedRow.normalizedValue.equals(validation.normalizedValue)) {
                    analysis.previewDto.getDuplicates()
                            .add(new DonneeImportRowIssueDto(rowNumber,
                                    "Cette combinaison de dimensions apparait deja dans le lot importé.",
                                    validation.dimensionsLabel, requestDto.getValeur()));
                    analysis.previewDto.getDiagnosticRows()
                            .add(buildDiagnosticRow(rowNumber, "duplicate",
                                    "Doublon dans le lot importé : cette combinaison existe déjà avec la même valeur.",
                                    validation.dimensionsLabel, requestDto.getValeur(), null, null,
                                    requestDto.getValeur()));
                } else {
                    analysis.previewDto.getRejected()
                            .add(new DonneeImportRowIssueDto(rowNumber,
                                    "Conflit dans le lot importé: mêmes dimensions avec une valeur différente.",
                                    validation.dimensionsLabel, requestDto.getValeur()));
                    analysis.previewDto.getDiagnosticRows()
                            .add(buildDiagnosticRow(rowNumber, "rejected",
                                    "Conflit dans le lot importé : mêmes dimensions avec une valeur différente.",
                                    validation.dimensionsLabel, requestDto.getValeur(), null, null,
                                    requestDto.getValeur()));
                }
                continue;
            }

            ExistingRowSnapshot existingRow = existingByKey.get(validation.dimensionKey);
            if (existingRow == null) {
                stagedByKey.put(validation.dimensionKey, currentRow);
                analysis.newRows.add(currentRow);
                analysis.previewDto.getDiagnosticRows()
                        .add(buildDiagnosticRow(rowNumber, "new", "Nouvelle ligne prête à être importée.",
                                validation.dimensionsLabel, requestDto.getValeur(), null, null,
                                requestDto.getValeur()));
                continue;
            }

            if (existingRow.normalizedValue.equals(validation.normalizedValue)) {
                analysis.previewDto.getDuplicates().add(new DonneeImportRowIssueDto(
                        rowNumber,
                        "Cette ligne existe deja avec la meme valeur.",
                        validation.dimensionsLabel,
                        requestDto.getValeur()));
                analysis.previewDto.getDiagnosticRows()
                        .add(buildDiagnosticRow(rowNumber, "duplicate",
                                "Doublon existant : la même combinaison existe déjà avec la même valeur.",
                                validation.dimensionsLabel, requestDto.getValeur(), existingRow.entity.getId(),
                                existingRow.entity.getValeur(), requestDto.getValeur()));
                continue;
            }

            ConflictImportRow conflictRow = new ConflictImportRow(currentRow, existingRow.entity);
            analysis.conflictRows.add(conflictRow);

            DonneeImportConflictDto conflictDto = new DonneeImportConflictDto();
            conflictDto.setRowNumber(rowNumber);
            conflictDto.setReason("Cette combinaison de dimensions existe deja avec une valeur differente.");
            conflictDto.setDimensionsLabel(validation.dimensionsLabel);
            conflictDto.setValeur(requestDto.getValeur());
            conflictDto.setExistingDonneeId(existingRow.entity.getId());
            conflictDto.setExistingValeur(existingRow.entity.getValeur());
            conflictDto.setImportedValeur(requestDto.getValeur());
            analysis.previewDto.getConflicts().add(conflictDto);
            analysis.previewDto.getDiagnosticRows()
                    .add(buildDiagnosticRow(rowNumber, "conflict",
                            "Conflit : cette combinaison existe déjà avec une valeur différente.",
                            validation.dimensionsLabel, requestDto.getValeur(), existingRow.entity.getId(),
                            existingRow.entity.getValeur(), requestDto.getValeur()));
        }

        analysis.previewDto.getDiagnosticRows().sort(Comparator.comparing(DonneeImportDiagnosedRowDto::getRowNumber));
        analysis.previewDto.setValidRows(
                analysis.newRows.size() + analysis.previewDto.getDuplicates().size() + analysis.conflictRows.size());
        analysis.previewDto.setDimensionsMapped(mappedDimensionIds.size());
        analysis.previewDto.setNewRows(analysis.newRows.size());
        analysis.previewDto.setDuplicateRows(analysis.previewDto.getDuplicates().size());
        analysis.previewDto.setConflictRows(analysis.conflictRows.size());
        analysis.previewDto.setRejectedRows(analysis.previewDto.getRejected().size());
        analysis.previewDto.setMinValue(minValue);
        analysis.previewDto.setMaxValue(maxValue);
        analysis.previewDto.setHasConflicts(!analysis.conflictRows.isEmpty());
        analysis.previewDto.setHasBlockingErrors(!analysis.previewDto.getRejected().isEmpty());

        return analysis;
    }

    private ValidationResult validateImportRow(DonneeIndicateurRequestDto requestDto, int rowNumber,
            Set<Long> allowedDimensionIds) {
        if (requestDto == null) {
            return ValidationResult.invalid("Ligne vide.", null, false, false);
        }

        String normalizedValue = normalizeComparisonValue(requestDto.getValeur());
        if (normalizedValue == null) {
            return ValidationResult.invalid("Valeur manquante.", buildDimensionsLabel(requestDto.getValeurDimensions()),
                    true, false);
        }

        if (CollectionUtils.isEmpty(requestDto.getValeurDimensions())) {
            return ValidationResult.invalid("Aucune dimension exploitable n'est fournie.",
                    buildDimensionsLabel(requestDto.getValeurDimensions()), false, true);
        }

        Set<Long> seenDimensionIds = new HashSet<>();
        List<NormalizedDimensionValue> normalizedDimensions = new ArrayList<>();

        for (ValeurDimensionRequestDto valeurDimension : requestDto.getValeurDimensions()) {
            if (valeurDimension == null || valeurDimension.getDimension() == null || valeurDimension.getDimension().getId() == null) {
                return ValidationResult.invalid("Une dimension est incomplete.",
                        buildDimensionsLabel(requestDto.getValeurDimensions()), false, true);
            }

            Long dimensionId = valeurDimension.getDimension().getId();
            if (!allowedDimensionIds.contains(dimensionId)) {
                return ValidationResult.invalid("Une dimension ne fait pas partie de cet indicateur.",
                        buildDimensionsLabel(requestDto.getValeurDimensions()), false, true);
            }

            if (!seenDimensionIds.add(dimensionId)) {
                return ValidationResult.invalid("La meme dimension est mappee plusieurs fois.",
                        buildDimensionsLabel(requestDto.getValeurDimensions()), false, true);
            }

            String normalizedDimensionValue = normalizeComparisonValue(valeurDimension.getValeur());
            if (normalizedDimensionValue == null) {
                return ValidationResult.invalid("Une valeur de dimension est vide.",
                        buildDimensionsLabel(requestDto.getValeurDimensions()), false, true);
            }

            normalizedDimensions.add(new NormalizedDimensionValue(
                    dimensionId,
                    safeDimensionLabel(valeurDimension),
                    normalizedDimensionValue,
                    normalizeStoredDimensionValue(valeurDimension.getValeur())));
        }

        if (!allowedDimensionIds.isEmpty() && seenDimensionIds.size() != allowedDimensionIds.size()) {
            return ValidationResult.invalid("Le mapping ne couvre pas toutes les dimensions attendues pour cet indicateur.",
                    buildDimensionsLabel(requestDto.getValeurDimensions()), false, true);
        }

        normalizedDimensions.sort(Comparator.comparing(NormalizedDimensionValue::dimensionId));
        String dimensionKey = normalizedDimensions.stream()
                .map(item -> item.dimensionId() + ":" + item.normalizedValue())
                .collect(Collectors.joining("|"));
        String dimensionsLabel = normalizedDimensions.stream()
                .map(item -> item.dimensionLabel() + ": " + item.storedValue())
                .collect(Collectors.joining(" | "));

        return ValidationResult.valid(
                dimensionKey,
                dimensionsLabel,
                seenDimensionIds,
                normalizedValue,
                parseNumericValue(normalizedValue));
    }

    private ExistingRowSnapshot toExistingRowSnapshot(DonneeIndicateur entity) {
        if (entity.getValeurDimensions() == null || entity.getValeurDimensions().isEmpty()) {
            return new ExistingRowSnapshot(entity, null, normalizeComparisonValue(entity.getValeur()));
        }

        String key = entity.getValeurDimensions().stream()
                .filter(valeurDimension -> valeurDimension.getDimension() != null && valeurDimension.getDimension().getId() != null)
                .sorted(Comparator.comparing(vd -> vd.getDimension().getId()))
                .map(vd -> vd.getDimension().getId() + ":" + normalizeComparisonValue(vd.getValeur()))
                .collect(Collectors.joining("|"));

        return new ExistingRowSnapshot(entity, key, normalizeComparisonValue(entity.getValeur()));
    }

    private String normalizeStoredValeur(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeStoredDimensionValue(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeComparisonValue(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        return normalized.toLowerCase(Locale.ROOT);
    }

    private Double parseNumericValue(String normalizedValue) {
        if (normalizedValue == null) {
            return null;
        }
        String numeric = normalizedValue.replace(" ", "").replace(",", ".");
        try {
            return Double.parseDouble(numeric);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Double updateMin(Double current, Double candidate) {
        if (candidate == null) {
            return current;
        }
        return current == null ? candidate : Math.min(current, candidate);
    }

    private Double updateMax(Double current, Double candidate) {
        if (candidate == null) {
            return current;
        }
        return current == null ? candidate : Math.max(current, candidate);
    }

    private String buildDimensionsLabel(List<ValeurDimensionRequestDto> valeurDimensions) {
        if (CollectionUtils.isEmpty(valeurDimensions)) {
            return "";
        }
        return valeurDimensions.stream()
                .filter(vd -> vd != null && vd.getDimension() != null)
                .map(vd -> safeDimensionLabel(vd) + ": " + Optional.ofNullable(vd.getValeur()).orElse(""))
                .collect(Collectors.joining(" | "));
    }

    private String safeDimensionLabel(ValeurDimensionRequestDto valeurDimension) {
        if (valeurDimension == null || valeurDimension.getDimension() == null) {
            return "Dimension";
        }
        if (valeurDimension.getDimension().getLibelle() != null && !valeurDimension.getDimension().getLibelle().isBlank()) {
            return valeurDimension.getDimension().getLibelle();
        }
        if (valeurDimension.getDimension().getNom() != null && !valeurDimension.getDimension().getNom().isBlank()) {
            return valeurDimension.getDimension().getNom();
        }
        return "Dimension";
    }

    private void copyPreview(DonneeImportCommitDto target, DonneeImportPreviewDto source) {
        target.setRowsReceived(source.getRowsReceived());
        target.setValidRows(source.getValidRows());
        target.setDimensionsMapped(source.getDimensionsMapped());
        target.setNewRows(source.getNewRows());
        target.setDuplicateRows(source.getDuplicateRows());
        target.setConflictRows(source.getConflictRows());
        target.setRejectedRows(source.getRejectedRows());
        target.setMissingValueRows(source.getMissingValueRows());
        target.setMissingDimensionsRows(source.getMissingDimensionsRows());
        target.setMinValue(source.getMinValue());
        target.setMaxValue(source.getMaxValue());
        target.setHasConflicts(source.isHasConflicts());
        target.setHasBlockingErrors(source.isHasBlockingErrors());
        target.setDiagnosticRows(source.getDiagnosticRows());
        target.setConflicts(source.getConflicts());
        target.setDuplicates(source.getDuplicates());
        target.setRejected(source.getRejected());
    }

    private DonneeImportDiagnosedRowDto buildDiagnosticRow(int rowNumber, String status, String reason,
            String dimensionsLabel, String valeur, Long existingDonneeId, String existingValeur, String importedValeur) {
        return new DonneeImportDiagnosedRowDto(
                rowNumber,
                status,
                reason,
                dimensionsLabel,
                valeur,
                existingDonneeId,
                existingValeur,
                importedValeur);
    }

    private static final class ImportAnalysis {
        private final DonneeImportPreviewDto previewDto = new DonneeImportPreviewDto();
        private final List<ValidatedImportRow> newRows = new ArrayList<>();
        private final List<ConflictImportRow> conflictRows = new ArrayList<>();
    }

    private record ExistingRowSnapshot(DonneeIndicateur entity, String dimensionKey, String normalizedValue) {
    }

    private record ValidatedImportRow(int rowNumber, DonneeIndicateurRequestDto requestDto, String dimensionKey,
            String dimensionsLabel, String normalizedValue) {
    }

    private record ConflictImportRow(ValidatedImportRow importRow, DonneeIndicateur existingDonnee) {
    }

    private record NormalizedDimensionValue(Long dimensionId, String dimensionLabel, String normalizedValue,
            String storedValue) {
    }

    private static final class ValidationResult {
        private final boolean valid;
        private final String reason;
        private final String dimensionKey;
        private final String dimensionsLabel;
        private final Set<Long> dimensionIds;
        private final String normalizedValue;
        private final Double numericValue;
        private final boolean missingValue;
        private final boolean missingDimensions;

        private ValidationResult(boolean valid, String reason, String dimensionKey, String dimensionsLabel,
                Set<Long> dimensionIds, String normalizedValue, Double numericValue, boolean missingValue,
                boolean missingDimensions) {
            this.valid = valid;
            this.reason = reason;
            this.dimensionKey = dimensionKey;
            this.dimensionsLabel = dimensionsLabel;
            this.dimensionIds = dimensionIds;
            this.normalizedValue = normalizedValue;
            this.numericValue = numericValue;
            this.missingValue = missingValue;
            this.missingDimensions = missingDimensions;
        }

        static ValidationResult invalid(String reason, String dimensionsLabel, boolean missingValue,
                boolean missingDimensions) {
            return new ValidationResult(false, reason, null, dimensionsLabel, Set.of(), null, null, missingValue,
                    missingDimensions);
        }

        static ValidationResult valid(String dimensionKey, String dimensionsLabel, Set<Long> dimensionIds,
                String normalizedValue, Double numericValue) {
            return new ValidationResult(true, null, dimensionKey, dimensionsLabel, dimensionIds, normalizedValue,
                    numericValue, false, false);
        }

        boolean isValid() {
            return valid;
        }
    }

    // @Override
    // public DonneeIndicateurDetailsDto getDonneeIndicateurWithTableData(Long
    // indicateurId, Long id, String tableFormat) {
    // // Get the donnee indicateur
    // DonneeIndicateur donneeIndicateur = findById(id)
    // .orElseThrow(() -> new EntityNotFoundException("DonneeIndicateur not found
    // with id: " + id));

    // // Verify it belongs to the specified indicateur
    // if (!donneeIndicateur.getIndicateur().getId().equals(indicateurId)) {
    // throw new EntityNotFoundException(
    // "DonneeIndicateur with id " + id + " does not belong to indicateur " +
    // indicateurId);
    // }

    // // Use the existing mapper to convert to DTO
    // DonneeIndicateurDetailsDto dto =
    // donneeIndicateurDetailMapper.mapToDto(donneeIndicateur);

    // // Add table data based on request format if specified
    // if (tableFormat != null && !tableFormat.trim().isEmpty()) {
    // Indicateur indicateur = donneeIndicateur.getIndicateur();

    // if ("pivot".equals(tableFormat)) {
    // dto.setPivotTableData(
    // IndicateurPivotDataTable.buildPivotSheetData(indicateur));
    // } else if ("flat".equals(tableFormat)) {
    // dto.setFlatTableData(
    // IndicateurFlatDataTable.buildFlatTableData(indicateur));
    // } else if ("crud".equals(tableFormat)) {
    // dto.setCrudTableData(
    // IndicateurCrudDataTable.buildCrudTableData(indicateur));
    // } else if ("create".equals(tableFormat)) {
    // dto.setCreateTemplateData(
    // IndicateurCrudDataTable.buildCreateTemplateData(indicateur));
    // } else if ("both".equals(tableFormat)) {
    // dto.setPivotTableData(
    // IndicateurPivotDataTable.buildPivotSheetData(indicateur));
    // dto.setFlatTableData(
    // IndicateurFlatDataTable.buildFlatTableData(indicateur));
    // } else if ("all".equals(tableFormat)) {
    // dto.setPivotTableData(
    // IndicateurPivotDataTable.buildPivotSheetData(indicateur));
    // dto.setFlatTableData(
    // IndicateurFlatDataTable.buildFlatTableData(indicateur));
    // dto.setCrudTableData(
    // IndicateurCrudDataTable.buildCrudTableData(indicateur));
    // dto.setCreateTemplateData(
    // IndicateurCrudDataTable.buildCreateTemplateData(indicateur));
    // }
    // }

    // return dto;
    // }
}
