package ma.org.ormt.seeder.data.chiffres;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.chiffres.dtos.ChiffreCleDto;
import ma.org.ormt.modules.chiffres.dtos.request.ChiffreCleRequestDto;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.chiffres.models.enums.KpiEvolutionMode;
import ma.org.ormt.modules.chiffres.models.enums.KpiFormatType;
import ma.org.ormt.modules.chiffres.models.enums.KpiModeSource;
import ma.org.ormt.modules.chiffres.services.ChiffreCleService;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.valeurdimension.models.ValeurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;
import ma.org.ormt.modules.roleacces.utils.RoleAccesMappingUtil;

@Log4j2
@Component
@Order(6)
@RequiredArgsConstructor
public class ChiffreCleSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.data_path}")
    private String dataExternalPath;

    private final ChiffreCleService chiffreCleService;

    private final IndicateurService indicateurService;

    private final RoleAccesService roleAccesService;

    private final ObjectMapper objectMapper;

    private static final String CHIFFRE_CLE_JSON_FILE = "chiffre_cle.json";
    private static final SeedStylePalette[] CAROUSEL_SEED_STYLES = new SeedStylePalette[] {
            new SeedStylePalette("#0f3c66", "#ffffff", "#1d4f80", "0 18px 36px rgba(15, 60, 102, 0.24)"),
            new SeedStylePalette("#d1d5db", "#111827", "#9ca3af", "0 16px 32px rgba(71, 85, 105, 0.16)"),
            new SeedStylePalette("#34d399", "#111827", "#10b981", "0 18px 34px rgba(16, 185, 129, 0.22)"),
            new SeedStylePalette("#0284c7", "#ffffff", "#0369a1", "0 18px 34px rgba(2, 132, 199, 0.22)")
    };

    /**
     * Executes the partner data seeding process when the application starts.
     * Only runs if seeding is enabled in the configuration.
     *
     * @param args Command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping chiffreCle data seeding.");
            return;
        }

        try {
            String initDataPath = dataExternalPath + "/init-data/chiffres";
            log.info("Using data path: {}", initDataPath);

            Path resourcePath = Paths.get(initDataPath);
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping chiffreCle data seeding.", resourcePath);
                return;
            }

            File jsonFile = new File(resourcePath.toFile(), CHIFFRE_CLE_JSON_FILE);
            if (!jsonFile.exists() || !jsonFile.isFile()) {
                log.warn("ChiffreCles JSON file not found at: {}", jsonFile.getAbsolutePath());
                return;
            }

            createChiffreClesFromJsonFile(jsonFile, initDataPath);

            log.info("ChiffreCle data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during chiffreCle data seeding: {}", e.getMessage(), e);
        }
    }

    @Transactional
    private void createChiffreCleFromIndicateurDonnee(String indicateurName) {
        Indicateur indicateur = indicateurService
                .findByNomWithDonneesAndDimensions(indicateurName)
                .orElse(null);
        if (indicateur == null) {
            log.warn("Indicateur not found: {}", indicateurName);
            return;
        }

        // Find the temporal dimension for this indicateur
        IndicateurDimension temporelleIndDim = indicateur.getIndicateurDimensions().stream()
                .filter(idim -> Boolean.TRUE.equals(idim.getTemporelle()))
                .findFirst()
                .orElse(null);
        if (temporelleIndDim == null) {
            log.warn("No temporal dimension found for Indicateur: {}", indicateur.getNom());
            return;
        }
        Long temporelleDimensionId = temporelleIndDim.getDimension().getId();
        // For each DonneeIndicateur, get the temporal value (e.g., year)
        DonneeIndicateur latestDonnee = indicateur.getDonnees().stream()
                .filter(donnee -> donnee.getValeurDimensions() != null)
                .filter(donnee -> donnee.getValeurDimensions().stream()
                        .anyMatch(vd -> vd.getDimension() != null
                                && vd.getDimension().getId().equals(temporelleDimensionId)))
                .max((d1, d2) -> {
                    String v1 = d1.getValeurDimensions().stream()
                            .filter(vd -> vd.getDimension() != null
                                    && vd.getDimension().getId().equals(temporelleDimensionId))
                            .map(vd -> vd.getValeur())
                            .findFirst().orElse("");
                    String v2 = d2.getValeurDimensions().stream()
                            .filter(vd -> vd.getDimension() != null
                                    && vd.getDimension().getId().equals(temporelleDimensionId))
                            .map(vd -> vd.getValeur())
                            .findFirst().orElse("");
                    // Try to compare as integer (e.g., year), fallback to string
                    try {
                        return Integer.compare(Integer.parseInt(v1), Integer.parseInt(v2));
                    } catch (NumberFormatException e) {
                        return v1.compareTo(v2);
                    }
                })
                .orElse(null);
        if (latestDonnee == null) {
            log.warn("No DonneeIndicateur with temporal value found for Indicateur: {}", indicateur.getNom());
            return;
        }
        // Create ChiffreCle from latestDonnee
        ChiffreCleRequestDto requestDto = new ChiffreCleRequestDto();
        requestDto.setLibelle(indicateur.getNom());
        requestDto.setDescription(indicateur.getDescription());
        requestDto.setValeur(latestDonnee.getValeur());
        requestDto.setUnite(indicateur.getUnite());
        requestDto.setAfficherDate(false);
        requestDto.setActif(Boolean.TRUE);
        requestDto.setModeSource(KpiModeSource.INDICATEUR_VALUE);
        requestDto.setFormatType("%".equals(indicateur.getUnite()) ? KpiFormatType.PERCENT : KpiFormatType.NUMBER);
        requestDto.setEvolutionMode(KpiEvolutionMode.NONE);
        requestDto.setStyleJson(resolveSeedStyleJson(0));
        requestDto.setIndicateur(buildReferenceDto(indicateur.getId()));
        requestDto.setDonneeIndicateur(buildReferenceDto(latestDonnee.getId()));
        try {
            chiffreCleService.create(requestDto);
            log.info("Created chiffreCle from DonneeIndicateur: {}", requestDto.getLibelle());
        } catch (Exception e) {
            log.error("Error creating chiffreCle from DonneeIndicateur: {}",
                    e.getMessage() + " for Indicateur: " + indicateur.getNom(), e);
        }
    }

    /**
     * Creates partners from a JSON file.
     *
     * @param jsonFile     The JSON file containing partner data
     * @param initDataPath The base path for initialization data
     */
    @Transactional
    private void createChiffreClesFromJsonFile(File jsonFile, String initDataPath) {
        try (InputStream inputStream = Files.newInputStream(jsonFile.toPath())) {
            log.info("Processing chiffreCles file: {}", jsonFile.getName());
            List<ChiffreCleDto> chiffreCleList = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<ChiffreCleDto>>() {
                    });

            if (chiffreCleList == null || chiffreCleList.isEmpty()) {
                log.warn("No chiffreCles found in file: {}", jsonFile.getName());
                return;
            }

            for (int index = 0; index < chiffreCleList.size(); index++) {
                ChiffreCleDto chiffreCle = chiffreCleList.get(index);
                try {
                    createChiffreCle(chiffreCle, initDataPath, index);
                } catch (Exception e) {
                    log.error("Error creating chiffreCle {}: {}",
                            chiffreCle != null ? chiffreCle.getLibelle() : "unknown", e.getMessage(), e);
                }
            }

        } catch (IOException e) {
            log.error("Error reading chiffreCles file {}: {}", jsonFile.getName(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error processing chiffreCles file {}: {}", jsonFile.getName(), e.getMessage(), e);
        }
    }

    /**
     * Creates a single partner in the database.
     *
     * @param chiffreCleDto The partner data from JSON
     * @param initDataPath  The base path for initialization data
     * @throws IOException If there's an error processing the image file
     */
    private void createChiffreCle(ChiffreCleDto chiffreCleDto, String initDataPath, int index) throws IOException {
        if (chiffreCleDto == null || !StringUtils.hasText(chiffreCleDto.getLibelle())) {
            log.warn("Skipping invalid chiffreCle data: missing name");
            return;
        }

        // Check if partner already exists
        Optional<ChiffreCle> existingChiffreCle = chiffreCleService.findByLibelle(chiffreCleDto.getLibelle());
        if (existingChiffreCle.isPresent()) {
            log.info("ChiffreCle with name '{}' already exists. Skipping.", chiffreCleDto.getLibelle());
            return;
        }

        ChiffreCleRequestDto requestDto = new ChiffreCleRequestDto();
        requestDto.setLibelle(chiffreCleDto.getLibelle());
        requestDto.setDescription(chiffreCleDto.getDescription());
        requestDto.setValeur(chiffreCleDto.getValeur());
        requestDto.setUnite(chiffreCleDto.getUnite());
        requestDto.setAccessType(chiffreCleDto.getAccessType());
        requestDto.setAfficherDate(chiffreCleDto.getAfficherDate());
        requestDto.setActif(chiffreCleDto.getActif());
        requestDto.setModeSource(
                chiffreCleDto.getModeSource() == null ? KpiModeSource.MANUAL : chiffreCleDto.getModeSource());
        requestDto.setFormatType(
                chiffreCleDto.getFormatType() == null ? KpiFormatType.NUMBER : chiffreCleDto.getFormatType());
        requestDto.setPrefixLabel(chiffreCleDto.getPrefixLabel());
        requestDto.setSuffixLabel(chiffreCleDto.getSuffixLabel());
        requestDto.setEvolutionMode(
                chiffreCleDto.getEvolutionMode() == null ? KpiEvolutionMode.NONE : chiffreCleDto.getEvolutionMode());
        requestDto.setMetadataJson(chiffreCleDto.getMetadataJson());
        requestDto.setStyleJson(StringUtils.hasText(chiffreCleDto.getStyleJson())
                ? chiffreCleDto.getStyleJson()
                : resolveSeedStyleJson(index));
        hydrateIndicatorReferences(requestDto, chiffreCleDto);

        try {
            ChiffreCle createdChiffreCle = chiffreCleService.create(requestDto);
            // Handle role accesses generically for the created resource
            RoleAccesMappingUtil.applyRoleAccesses(roleAccesService, chiffreCleDto.getRoleAcces(),
                    "chiffreCle",
                    createdChiffreCle.getId(),
                    ra -> ra.getRoleCode(),
                    ra -> ra.getNiveauAcces(),
                    "lecture");

            log.info("Created chiffreCle: {}", chiffreCleDto.getLibelle());
        } catch (Exception e) {
            log.error("Error creating chiffreCle {}: {}", chiffreCleDto.getLibelle(), e.getMessage());
        }
    }

    private void hydrateIndicatorReferences(ChiffreCleRequestDto requestDto, ChiffreCleDto chiffreCleDto) {
        KpiModeSource modeSource = requestDto.getModeSource() == null ? KpiModeSource.MANUAL
                : requestDto.getModeSource();
        if (modeSource != KpiModeSource.INDICATEUR_VALUE) {
            return;
        }

        if (requestDto.getIndicateur() != null && requestDto.getIndicateur().getId() != null
                && requestDto.getDonneeIndicateur() != null && requestDto.getDonneeIndicateur().getId() != null) {
            return;
        }

        String indicateurNom = firstNonBlank(
                chiffreCleDto.getIndicateurNom(),
                chiffreCleDto.getDonneeReference() != null ? chiffreCleDto.getDonneeReference().getIndicateurNom()
                        : null);
        if (!StringUtils.hasText(indicateurNom)) {
            return;
        }

        Indicateur indicateur = indicateurService.findByNomWithDonneesAndDimensions(indicateurNom).orElse(null);
        if (indicateur == null) {
            log.warn("Unable to resolve indicateur '{}' for chiffreCle '{}'.", indicateurNom,
                    chiffreCleDto.getLibelle());
            return;
        }

        requestDto.setIndicateur(buildReferenceDto(indicateur.getId()));

        DonneeIndicateur resolvedDonnee = resolveDonneeReference(indicateur, chiffreCleDto);
        if (resolvedDonnee != null) {
            requestDto.setDonneeIndicateur(buildReferenceDto(resolvedDonnee.getId()));
        }
    }

    private DonneeIndicateur resolveDonneeReference(Indicateur indicateur, ChiffreCleDto chiffreCleDto) {
        if (indicateur.getDonnees() == null || indicateur.getDonnees().isEmpty()) {
            return null;
        }

        ChiffreCleDto.DonneeReferenceDto reference = chiffreCleDto.getDonneeReference();
        if (reference == null) {
            return chiffreCleDto.getDonneeIndicateur() == null ? null : matchLegacyDonneeDto(indicateur, chiffreCleDto);
        }

        return indicateur.getDonnees().stream()
                .filter(donnee -> valuesEqual(donnee.getValeur(), reference.getValeur()))
                .filter(donnee -> matchesDimensionReference(donnee, reference.getDimensions()))
                .findFirst()
                .orElseGet(() -> {
                    log.warn("Unable to resolve donnee reference for chiffreCle '{}' and indicateur '{}'.",
                            chiffreCleDto.getLibelle(), indicateur.getNom());
                    return null;
                });
    }

    private DonneeIndicateur matchLegacyDonneeDto(Indicateur indicateur, ChiffreCleDto chiffreCleDto) {
        if (chiffreCleDto.getDonneeIndicateur() == null) {
            return null;
        }

        List<ChiffreCleDto.DimensionValueDto> dimensions = chiffreCleDto.getDonneeIndicateur()
                .getValeurDimensions() == null
                        ? List.of()
                        : chiffreCleDto.getDonneeIndicateur().getValeurDimensions().stream()
                                .map(value -> new ChiffreCleDto.DimensionValueDto(
                                        value.getDimension() != null ? value.getDimension().getNom() : null,
                                        value.getValeur()))
                                .toList();

        return indicateur.getDonnees().stream()
                .filter(donnee -> valuesEqual(donnee.getValeur(), chiffreCleDto.getDonneeIndicateur().getValeur()))
                .filter(donnee -> matchesDimensionReference(donnee, dimensions))
                .findFirst()
                .orElse(null);
    }

    private boolean matchesDimensionReference(DonneeIndicateur donnee,
            List<ChiffreCleDto.DimensionValueDto> dimensions) {
        List<ChiffreCleDto.DimensionValueDto> expectedDimensions = dimensions == null ? List.of() : dimensions;
        Map<String, String> expected = expectedDimensions.stream()
                .filter(dimension -> StringUtils.hasText(dimension.getDimensionNom()))
                .collect(Collectors.toMap(
                        dimension -> normalizeKey(dimension.getDimensionNom()),
                        dimension -> normalizeValue(dimension.getValeur()),
                        (left, _right) -> left));

        Map<String, String> actual = donnee.getValeurDimensions() == null ? Map.of()
                : donnee.getValeurDimensions().stream()
                        .filter(value -> value.getDimension() != null
                                && StringUtils.hasText(value.getDimension().getNom()))
                        .collect(Collectors.toMap(
                                value -> normalizeKey(value.getDimension().getNom()),
                                value -> normalizeValue(value.getValeur()),
                                (left, _right) -> left));

        return actual.equals(expected);
    }

    private boolean valuesEqual(String left, String right) {
        return normalizeValue(left).equals(normalizeValue(right));
    }

    private String normalizeKey(String value) {
        return normalizeValue(value).toLowerCase(Locale.ROOT);
    }

    private String normalizeValue(String value) {
        return value == null ? "" : value.trim();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private String resolveSeedStyleJson(int index) {
        SeedStylePalette palette = CAROUSEL_SEED_STYLES[Math.floorMod(index, CAROUSEL_SEED_STYLES.length)];
        return palette.toStyleJson();
    }

    private Dto buildReferenceDto(Long id) {
        Dto dto = new Dto();
        dto.setId(id);
        return dto;
    }

    private record SeedStylePalette(String backgroundColor, String textColor, String borderColor, String shadow) {
        private String toStyleJson() {
            return """
                    {"backgroundColor":"%s","textColor":"%s","borderTopColor":"%s","borderRightColor":"%s","borderBottomColor":"%s","borderLeftColor":"%s","borderTopWidth":1,"borderRightWidth":1,"borderBottomWidth":1,"borderLeftWidth":1,"borderTopLeftRadius":0,"borderTopRightRadius":0,"borderBottomRightRadius":0,"borderBottomLeftRadius":0,"paddingTop":4,"paddingRight":4,"paddingBottom":4,"paddingLeft":4,"shadow":"%s","textAlign":"center"}
                    """
                    .formatted(
                            backgroundColor,
                            textColor,
                            borderColor,
                            borderColor,
                            borderColor,
                            borderColor,
                            shadow);
        }
    }
}
