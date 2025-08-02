package ma.org.ormt.seeder.data.graphe;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
@Order(5)
@RequiredArgsConstructor
public class GrapheOffreDeTravailSeeder implements CommandLineRunner {

        @Value("${starter.database.seed}")
        private boolean seeding;

        private final GrapheTypeService grapheTypeService;
        private final IndicateurService indicateurService;
        private final GrapheConfigurationService grapheConfigurationService;

        /**
         * Executes the graphe configuration data seeding process when the application
         * starts.
         * Only runs if seeding is enabled in the configuration.
         *
         * @param args Command line arguments (not used)
         */
        @Override
        public void run(String... args) {
                if (!seeding) {
                        log.info("Seeding is disabled. Skipping graphe configuration data seeding.");
                        return;
                }

                try {

                        createIndicateurGrapheConfigurationsOffreDeTravail();
                        log.info("Graphe configuration data seeding completed successfully.");
                } catch (Exception e) {
                        log.error("Error during graphe configuration data seeding: {}", e.getMessage(), e);
                }
        }

        /**
         * Creates graphe configurations for "Offre de travail" indicators.
         */
        @Transactional
        private void createIndicateurGrapheConfigurationsOffreDeTravail() {
                log.info("Creating graphe configurations for Offre de travail indicators...");

                List<IndicateurGrapheMapping> mappings = Arrays.asList(
                                new IndicateurGrapheMapping("nouveaux entrants potentiels sur le mt par genre",
                                                "CAMEMBERT"),
                                new IndicateurGrapheMapping("nouveaux entrants potentiels sur le mt par composante",
                                                "CAMEMBERT"),
                                new IndicateurGrapheMapping("population en âge d'activité", "HISTOGRAMME"),
                                new IndicateurGrapheMapping("population inactive", "HISTOGRAMME"),
                                new IndicateurGrapheMapping("population active", "HISTOGRAMME"),
                                new IndicateurGrapheMapping("population active par diplôme et genre", "HISTOGRAMME"),
                                new IndicateurGrapheMapping("population en active par diplôme et milieu de résidence",
                                                "HISTOGRAMME"),
                                new IndicateurGrapheMapping("taux d'activité selon le genre et le milieu de résidence",
                                                "COURBES"),
                                new IndicateurGrapheMapping("taux d'activité selon le genre", "COURBES"),
                                new IndicateurGrapheMapping(
                                                "taux d'activité selon le groupe d'âge et le milieu de résidence",
                                                "COURBES"),
                                new IndicateurGrapheMapping("taux d'activité selon le groupe d'âge et le genre",
                                                "PYRAMIDE_DES_AGES"),
                                new IndicateurGrapheMapping(
                                                "taux d'activité selon le diplôme et le milieu de résidence",
                                                "COURBES"),
                                new IndicateurGrapheMapping("taux d'activité selon le diplôme et le genre", "COURBES"),
                                new IndicateurGrapheMapping("taux d'activité selon le milieu de résidence", "COURBES"),
                                new IndicateurGrapheMapping("taux d'activité par région et le milieu de résidence",
                                                "COURBES"),
                                new IndicateurGrapheMapping("taux d'activité par région et genre", "COURBES"),
                                new IndicateurGrapheMapping("population active en chômage par milieu de résidence",
                                                "HISTOGRAMME"),
                                new IndicateurGrapheMapping("population active en chômage par diplôme et genre",
                                                "HISTOGRAMME"),
                                new IndicateurGrapheMapping(
                                                "population active en chômage par diplôme et milieu de résidence",
                                                "HISTOGRAMME"),
                                new IndicateurGrapheMapping("population active en chômage par genre", "HISTOGRAMME"),
                                new IndicateurGrapheMapping("population active en chômage selon la durée",
                                                "HISTOGRAMME"),
                                new IndicateurGrapheMapping("population active en chômage selon les causes",
                                                "HISTOGRAMME"),
                                new IndicateurGrapheMapping("taux de chômage selon le milieu de résidence",
                                                "HISTOGRAMME"),
                                new IndicateurGrapheMapping("taux de chômage par région et genre", "CARTE"),
                                new IndicateurGrapheMapping("taux de chômage par région et milieu de résidence",
                                                "COURBES"),
                                new IndicateurGrapheMapping("taux de chômage selon le groupe d'âge", "COURBES"),
                                new IndicateurGrapheMapping("taux de chômage selon le diplôme", "COURBES"),
                                new IndicateurGrapheMapping("taux de chômage selon le genre", "COURBES"),
                                new IndicateurGrapheMapping("effectif des jeunes neet par milieu de résidence",
                                                "HISTOGRAMME"),
                                new IndicateurGrapheMapping("effectif des jeunes neet par genre", "HISTOGRAMME"),
                                new IndicateurGrapheMapping("taux de neet par milieu de résidence", "COURBES"),
                                new IndicateurGrapheMapping("taux de neet par genre", "COURBES"),
                                new IndicateurGrapheMapping("taux de neet par région", "CARTE"));

                for (IndicateurGrapheMapping mapping : mappings) {
                        try {
                                createGrapheConfigurationForIndicateur(mapping);
                        } catch (Exception e) {
                                log.error("Error creating configuration for indicator '{}': {}",
                                                mapping.indicateurNom, e.getMessage());
                        }
                }
        }

        /**
         * Creates a graphe configuration for a specific indicator mapping.
         */
        private void createGrapheConfigurationForIndicateur(IndicateurGrapheMapping mapping) {
                // Skip if no graph type is specified
                if (mapping.grapheTypeCode == null || mapping.grapheTypeCode.trim().isEmpty()) {
                        log.info("Skipping indicator '{}' - no graph type specified", mapping.indicateurNom);
                        return;
                }

                // Find the indicator by name
                Optional<Indicateur> indicateurOpt = indicateurService.findByNom(mapping.indicateurNom);
                if (indicateurOpt.isEmpty()) {
                        log.warn("Indicator not found: '{}'", mapping.indicateurNom);
                        return;
                }

                // Find the graph type by code
                Optional<GrapheType> grapheTypeOpt = findGrapheTypeByCode(mapping.grapheTypeCode);
                if (grapheTypeOpt.isEmpty()) {
                        log.warn("Graph type not found with code: '{}'", mapping.grapheTypeCode);
                        return;
                }

                Indicateur indicateur = indicateurOpt.get();
                GrapheType grapheType = grapheTypeOpt.get();

                // Generate unique configuration name with indicateur name and configuration
                // number
                String baseConfigurationName = grapheType.getNom() + " - " + indicateur.getNom();
                String configurationName = generateUniqueConfigurationName(baseConfigurationName);

                // Create the configuration
                GrapheConfiguration configuration = GrapheConfiguration.builder()
                                .indicateur(indicateur)
                                .grapheType(grapheType)
                                .nom(configurationName)
                                .dimensionMappingJson("{\"default\": \"standard\"}")
                                .chartOptionsJson(null)
                                .isDefault(true)
                                .build();

                GrapheConfiguration savedConfig = grapheConfigurationService.save(configuration);
                log.info("Created graphe configuration: {} (ID: {})", savedConfig.getNom(), savedConfig.getId());
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
                        case "GRAPHIQUE_EN_SECTEURS":
                        case "GRAPHIQUE EN SECTEURS":
                                return "Graphique en secteurs";
                        case "NUAGE_DU_POINT":
                        case "NUAGE DU POINT":
                                return "Nuage de points";
                        case "RADAR":
                                return "Radar";
                        case "PYRAMIDE_DES_AGES":
                        case "PYRAMIDE DES AGES":
                                return "Pyramide des âges";
                        case "INDICATEUR":
                                return null; // No graph for simple indicators
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
         * Creates multiple graphe configurations for a single indicator.
         */
        private void createMultipleGrapheConfigurationsForIndicateur(IndicateurMultipleGrapheMapping mapping) {
                // Find the indicator by name
                Optional<Indicateur> indicateurOpt = indicateurService.findByNom(mapping.indicateurNom);
                if (indicateurOpt.isEmpty()) {
                        log.warn("Indicator not found: '{}'", mapping.indicateurNom);
                        return;
                }

                Indicateur indicateur = indicateurOpt.get();

                // Create configuration for each graph type
                boolean isFirst = true;
                for (String grapheTypeCode : mapping.grapheTypeCodes) {
                        try {
                                createSingleGrapheConfiguration(indicateur, grapheTypeCode, isFirst);
                                isFirst = false;
                        } catch (Exception e) {
                                log.error("Error creating configuration for indicator '{}' with graph type '{}': {}",
                                                mapping.indicateurNom, grapheTypeCode, e.getMessage());
                        }
                }
        }

        /**
         * Creates a single graphe configuration for an indicator and graph type.
         */
        private void createSingleGrapheConfiguration(Indicateur indicateur, String grapheTypeCode, boolean isDefault) {
                // Find the graph type by code
                Optional<GrapheType> grapheTypeOpt = findGrapheTypeByCode(grapheTypeCode);
                if (grapheTypeOpt.isEmpty()) {
                        log.warn("Graph type not found with code: '{}'", grapheTypeCode);
                        return;
                }

                GrapheType grapheType = grapheTypeOpt.get();

                // Generate unique configuration name
                String baseConfigurationName = grapheType.getNom() + " - " + indicateur.getNom();
                String configurationName = generateUniqueConfigurationName(baseConfigurationName);

                // Create the configuration
                GrapheConfiguration configuration = GrapheConfiguration.builder()
                                .indicateur(indicateur)
                                .grapheType(grapheType)
                                .nom(configurationName)
                                .dimensionMappingJson("{\"default\": \"standard\"}")
                                .chartOptionsJson(null)
                                .isDefault(isDefault)
                                .build();

                GrapheConfiguration savedConfig = grapheConfigurationService.save(configuration);
                log.info("Created graphe configuration: {} (ID: {})", savedConfig.getNom(), savedConfig.getId());
        }

        /**
         * Helper class to hold indicator-graph type mapping data
         */
        private static class IndicateurGrapheMapping {
                final String indicateurNom;
                final String grapheTypeCode;

                IndicateurGrapheMapping(String indicateurNom, String grapheTypeCode) {
                        this.indicateurNom = indicateurNom;
                        this.grapheTypeCode = grapheTypeCode;
                }
        }

        /**
         * Helper class to hold indicator with multiple graph types mapping data
         */
        private static class IndicateurMultipleGrapheMapping {
                final String indicateurNom;
                final List<String> grapheTypeCodes;

                IndicateurMultipleGrapheMapping(String indicateurNom, List<String> grapheTypeCodes) {
                        this.indicateurNom = indicateurNom;
                        this.grapheTypeCodes = grapheTypeCodes;
                }
        }
}