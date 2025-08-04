package ma.org.ormt.seeder.data.graphe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.indicateurs.graphe.configuration.models.GrapheConfiguration;
import ma.org.ormt.modules.indicateurs.graphe.configuration.services.GrapheConfigurationService;
import ma.org.ormt.modules.indicateurs.graphe.type.models.GrapheType;
import ma.org.ormt.modules.indicateurs.graphe.type.services.GrapheTypeService;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@Log4j2
@Component
@Order(6) // Run after indicator seeders
@RequiredArgsConstructor
public class JsonGrapheConfigurationSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${starter.database.init-data-path:data/init-data}")
    private String initDataPath;

    private final GrapheTypeService grapheTypeService;
    private final IndicateurService indicateurService;
    private final GrapheConfigurationService grapheConfigurationService;
    private final ObjectMapper objectMapper;

    /**
     * Executes the JSON-based graphe configuration data seeding process when the
     * application starts.
     * Only runs if seeding is enabled in the configuration.
     *
     * @param args Command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping JSON graph configuration seeding.");
            return;
        }

        try {
            processJsonFiles();
            log.info("JSON graph configuration seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during JSON graph configuration seeding: {}", e.getMessage(), e);
        }
    }

    /**
     * Processes all JSON files in the domaines directory to extract graph
     * configurations.
     */
    @Transactional
    private void processJsonFiles() throws IOException {
        Path domainesPath = Paths.get(initDataPath, "domaines");

        if (!Files.exists(domainesPath)) {
            log.warn("Domaines directory not found: {}", domainesPath);
            return;
        }

        log.info("Processing JSON files from: {}", domainesPath);

        try (Stream<Path> paths = Files.walk(domainesPath)) {
            paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(this::processJsonFile);
        }
    }

    /**
     * Processes a single JSON file to extract and create graph configurations.
     */
    private void processJsonFile(Path jsonFile) {
        try {
            log.debug("Processing JSON file: {}", jsonFile);
            JsonNode rootNode = objectMapper.readTree(jsonFile.toFile());

            JsonNode indicateursNode = rootNode.get("indicateurs");
            if (indicateursNode != null && indicateursNode.isArray()) {
                for (JsonNode indicateurNode : indicateursNode) {
                    processIndicateurGrapheConfigurations(indicateurNode);
                }
            }
        } catch (Exception e) {
            log.error("Error processing JSON file {}: {}", jsonFile, e.getMessage());
        }
    }

    /**
     * Processes graph configurations for a single indicator from JSON.
     */
    private void processIndicateurGrapheConfigurations(JsonNode indicateurNode) {
        String indicateurNom = indicateurNode.get("nom").asText();
        JsonNode grapheConfigsNode = indicateurNode.get("grapheConfigurations");

        if (grapheConfigsNode == null || !grapheConfigsNode.isArray()) {
            log.debug("No graph configurations found for indicator: {}", indicateurNom);
            return;
        }

        // Find the indicator
        Optional<Indicateur> indicateurOpt = indicateurService.findByNom(indicateurNom);
        if (indicateurOpt.isEmpty()) {
            log.warn("Indicator not found: '{}'", indicateurNom);
            return;
        }

        Indicateur indicateur = indicateurOpt.get();

        // Process each graph configuration
        for (JsonNode configNode : grapheConfigsNode) {
            createGrapheConfigurationFromJson(indicateur, configNode);
        }
    }

    /**
     * Creates a graph configuration from JSON node data.
     */
    private void createGrapheConfigurationFromJson(Indicateur indicateur, JsonNode configNode) {
        try {
            String grapheTypeCode = configNode.get("grapheTypeCode").asText();
            String configNom = configNode.has("nom") ? configNode.get("nom").asText() : null;
            String dimensionMapping = configNode.has("dimensionMappingJson")
                    ? configNode.get("dimensionMappingJson").asText()
                    : "{\"default\": \"standard\"}";
            String chartOptions = configNode.has("chartOptionsJson") && !configNode.get("chartOptionsJson").isNull()
                    ? configNode.get("chartOptionsJson").asText()
                    : null;
            boolean isDefault = configNode.has("isDefault") ? configNode.get("isDefault").asBoolean() : false;

            // Find graph type
            Optional<GrapheType> grapheTypeOpt = findGrapheTypeByCode(grapheTypeCode);
            if (grapheTypeOpt.isEmpty()) {
                log.warn("Graph type not found with code: '{}'", grapheTypeCode);
                return;
            }

            GrapheType grapheType = grapheTypeOpt.get();

            // Generate name if not provided
            if (configNom == null || configNom.trim().isEmpty()) {
                configNom = grapheType.getNom() + " - " + indicateur.getNom();
            }

            // Make sure name is unique
            String uniqueConfigName = generateUniqueConfigurationName(configNom);

            // Check if configuration already exists
            if (configurationAlreadyExists(indicateur, grapheType)) {
                log.debug("Graph configuration already exists for indicator '{}' and graph type '{}'",
                        indicateur.getNom(), grapheType.getNom());
                return;
            }

            // Create configuration
            GrapheConfiguration configuration = GrapheConfiguration.builder()
                    .indicateur(indicateur)
                    .grapheType(grapheType)
                    .nom(uniqueConfigName)
                    .dimensionMappingJson(dimensionMapping)
                    .chartOptionsJson(chartOptions)
                    .isDefault(isDefault)
                    .build();

            GrapheConfiguration savedConfig = grapheConfigurationService.save(configuration);
            log.info("Created graph configuration: {} (ID: {})", savedConfig.getNom(), savedConfig.getId());

        } catch (Exception e) {
            log.error("Error creating graph configuration for indicator '{}': {}",
                    indicateur.getNom(), e.getMessage());
        }
    }

    /**
     * Finds a GrapheType by its code.
     * Uses a mapping from French names to codes if direct lookup fails.
     */
    private Optional<GrapheType> findGrapheTypeByCode(String codeOrName) {
        // First try direct lookup by name
        Optional<GrapheType> result = grapheTypeService.findByNom(codeOrName);
        if (result.isPresent()) {
            return result;
        }

        // Map French display names to internal codes
        String mappedCode = mapFrenchNameToCode(codeOrName);
        if (mappedCode != null) {
            return grapheTypeService.findByNom(mappedCode);
        }

        return Optional.empty();
    }

    /**
     * Maps French display names to internal graph type codes.
     */
    private String mapFrenchNameToCode(String frenchName) {
        switch (frenchName.toUpperCase()) {
            case "HISTOGRAMME":
                return "Histogramme";
            case "COURBES":
                return "Courbes";
            case "COURBE_LINEAIRE":
            case "COURBE LINEAIRE":
                return "Courbe linéaire";
            case "CARTE":
                return "Carte";
            case "CAMEMBERT":
                return "Camembert";
            case "HISTOGRAMME_EMPILTE_EVOLUTION":
            case "HISTOGRAMME EMPILTE EVOLUTION":
                return "Histogramme empilé évolution";
            case "COURBE_EVOLUTION":
            case "COURBE EVOLUTION":
                return "Courbe linéaire évolution";
            case "INDICATEUR":
                return "indicateur";
            default:
                return null;
        }
    }

    /**
     * Generates a unique configuration name by adding a number suffix if needed.
     * Format: "GrapheType - IndicateurName" or "GrapheType - IndicateurName (2)",
     * etc.
     */
    private String generateUniqueConfigurationName(String baseName) {
        // First try the base name without number
        if (grapheConfigurationService.findByNom(baseName).isEmpty()) {
            return baseName;
        }

        // If base name exists, try with numbers starting from 2
        int counter = 2;
        String uniqueName;
        do {
            uniqueName = baseName + " (" + counter + ")";
            counter++;
        } while (grapheConfigurationService.findByNom(uniqueName).isPresent());

        return uniqueName;
    }

    /**
     * Checks if a configuration already exists for the given indicator and graph
     * type.
     */
    private boolean configurationAlreadyExists(Indicateur indicateur, GrapheType grapheType) {
        return indicateur.getGrapheConfigurations() != null &&
                indicateur.getGrapheConfigurations().stream()
                        .anyMatch(config -> config.getGrapheType().getId().equals(grapheType.getId()));
    }
}
