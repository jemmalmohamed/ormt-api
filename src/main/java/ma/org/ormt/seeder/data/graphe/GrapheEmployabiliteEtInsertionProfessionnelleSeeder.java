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
public class GrapheEmployabiliteEtInsertionProfessionnelleSeeder implements CommandLineRunner {

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
                        createIndicateurGrapheConfigurationsEmployabiliteEtInsertionProfessionnelle();
                        log.info("Graphe configuration data seeding completed successfully.");
                } catch (Exception e) {
                        log.error("Error during graphe configuration data seeding: {}", e.getMessage(), e);
                }
        }

        /**
         * Creates graphe configurations for employability and professional insertion
         * indicators.
         */
        @Transactional
        private void createIndicateurGrapheConfigurationsEmployabiliteEtInsertionProfessionnelle() {
                log.info("Creating graphe configurations for employability and professional insertion indicators...");

                List<IndicateurMultipleGrapheMapping> mappings = Arrays.asList(
                                // Tahfiz beneficiaries
                                new IndicateurMultipleGrapheMapping("nombre des salariés bénéficiaires tahfiz",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("salariés bénéficiaires tahfiz par genre",
                                                Arrays.asList("CAMEMBERT", "COURBE_LINEAIRE",
                                                                "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("salariés bénéficiaires tahfiz par âge",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("salariés bénéficiaires tahfiz par diplôme",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("salariés bénéficiaires tahfiz par sae",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping(
                                                "salariés bénéficiaires tahfiz par taille d'entreprise",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("salariés bénéficiaires tahfiz par région",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("nombre d'entreprises bénéficiaires tahfiz",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping(
                                                "entreprises bénéficiaires tahfiz par taille d'entreprise",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("entreprises bénéficiaires tahfiz par sae",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("entreprises bénéficiaires tahfiz par région",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),

                                // CI (Contrats d'insertion) beneficiaries
                                new IndicateurMultipleGrapheMapping("nombre des bénéficiaires du contrats d'insertion",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("bénéficiaires du ci par genre",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("bénéficiaires du ci par diplôme",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("bénéficiaires du ci par sae",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("bénéficiaires du ci par région",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("nombre d'entreprises bénéficiaires du ci",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping(
                                                "entreprises bénéficiaires du ci par taille d'entreprise",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("entreprises bénéficiaires du ci par sae",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("entreprises bénéficiaires du ci par région",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),

                                // PI beneficiaries
                                new IndicateurMultipleGrapheMapping("nombre des bénéficiaires du pi",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("bénéficiaires du pi par pays d'accueil",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("bénéficiaires du pi par sae",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("bénéficiaires du pi par type de contrat",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("bénéficiaires du pi par région",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),

                                // Project support
                                new IndicateurMultipleGrapheMapping("objectif ciblé (accompagnement)",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("nombre de porteurs de projet accompagnés",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("porteurs de projet accompagnés par genre",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("porteurs de projet accompagnés par âge",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("porteurs de projet accompagnés par région",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),

                                // Enterprise creation
                                new IndicateurMultipleGrapheMapping("nombre d'entreprises crées",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("entreprises crées par genre",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("entreprises crées par région",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("nombre des emplois crées",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("emplois crées par région",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),

                                // Employment targets
                                new IndicateurMultipleGrapheMapping("objectif ciblé",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("taux de réalisation de l'objectif ciblé",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("nombre des bénéficiaires de l'emploi salarié",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("emploi salarié par genre",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("emploi salarié par région",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),

                                // Training programs
                                new IndicateurMultipleGrapheMapping("objectif ciblé (hors formation en ligne)",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping(
                                                "nombre de bénéficiaires des programmes d'amélioration de l'employabilité",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("bénéficiaires des pae par composante",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("bénéficiaires de la fce par région",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("bénéficiaires de la fqr par région",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("bénéficiaires de la fse par région",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping(
                                                "bénéficiaires de la fse par secteur d'activité économique",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),

                                // Partnerships and online training
                                new IndicateurMultipleGrapheMapping("nombre des bénéficiaires du partenariat régional",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("nombre des bénéficiaires du partenariat national",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("nombre des bénéficiaires de la formation en ligne",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),

                                // Insertion rates
                                new IndicateurMultipleGrapheMapping("taux d'insertion à 3 ans par genre",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("taux d'insertion à 3 ans par âge",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("taux d'insertion à 3 ans par diplôme",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("taux d'insertion à 3 ans par taille d'entreprise",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping(
                                                "taux d'insertion à 3 ans par secteur d'activité économique",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("taux d'insertion à 3 ans par région",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping(
                                                "taux d'insertion à 3 ans selon la période de stage",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping(
                                                "taux d'insertion à l'issue de stage par taille d'entreprise",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping(
                                                "taux d'insertion à l'issue de stage par secteur d'activité économique",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),

                                // IDMAJ program
                                new IndicateurMultipleGrapheMapping("nombre des bénéficiaires idmaj, y compris le pi",
                                                Arrays.asList("HISTOGRAMME", "COURBE_LINEAIRE")),
                                new IndicateurMultipleGrapheMapping("bénéficiaires idmaj par genre (y compris le pi)",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("bénéficiaires idmaj par âge (hors pi)",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("bénéficiaires idmaj par diplôme (hors pi)",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("bénéficiaires idmaj par type de contrat (hors pi)",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping(
                                                "bénéficiaires idmaj par secteur d'activité (hors pi)",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")),
                                new IndicateurMultipleGrapheMapping("bénéficiaires idmaj par région (hors pi)",
                                                Arrays.asList("CAMEMBERT", "HISTOGRAMME_EMPILTE_EVOLUTION")));

                for (IndicateurMultipleGrapheMapping mapping : mappings) {
                        try {
                                createMultipleGrapheConfigurationsForIndicateur(mapping);
                        } catch (Exception e) {
                                log.error("Error creating configurations for indicator '{}': {}",
                                                mapping.indicateurNom, e.getMessage());
                        }
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
                                .isDefault(isDefault) // true for first, false for others in multiple
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