package ma.org.ormt.seeder.data.graphe;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.indicateurs.graphe.type.models.GrapheType;
import ma.org.ormt.modules.indicateurs.graphe.type.services.GrapheTypeService;

@Log4j2
@Component
@Order(4)
@RequiredArgsConstructor
public class GrapheTypeSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    private final GrapheTypeService grapheTypeService;

    /**
     * Executes the graphe type data seeding process when the application starts.
     * Only runs if seeding is enabled in the configuration.
     *
     * @param args Command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping graphe type data seeding.");
            return;
        }

        try {
            createBasicGrapheTypes();
            log.info("Graphe type data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during graphe type data seeding: {}", e.getMessage(), e);
        }
    }

    /**
     * Creates the basic graph types needed for the system.
     */
    @Transactional
    private void createBasicGrapheTypes() {
        log.info("Creating basic graphe types...");

        List<GrapheTypeData> grapheTypesData = Arrays.asList(
                new GrapheTypeData("CAMEMBERT", "Camembert", "Graphique circulaire pour représenter des proportions",
                        "pie"),
                new GrapheTypeData("HISTOGRAMME", "Histogramme", "Graphique en barres verticales", "bar"),
                new GrapheTypeData("COURBES", "Courbes", "Graphique linéaire simple", "line"),
                new GrapheTypeData("PYRAMIDE_AGES", "Pyramide des âges",
                        "Graphique spécialisé pour la répartition par âge et genre", "bar"),
                new GrapheTypeData("CARTE", "Carte", "Représentation géographique des données", "choropleth"),
                new GrapheTypeData("COURBE_LINEAIRE", "Courbe linéaire",
                        "Graphique linéaire pour évolutions temporelles", "line"),
                new GrapheTypeData("HISTOGRAMME_EMPILTE_EVOLUTION", "Histogramme empilé évolution",
                        "Histogramme empilé pour comparer évolutions", "bar"),
                new GrapheTypeData("COURBE_EVOLUTION", "Courbe linéaire évolution",
                        "Courbes multiples pour évolutions temporelles", "line"));

        for (GrapheTypeData data : grapheTypesData) {
            try {
                // Check if type already exists by code
                if (grapheTypeService.findByNom(data.nom).isEmpty()) {
                    GrapheType grapheType = GrapheType.builder()
                            .code(data.code)
                            .nom(data.nom)
                            .description(data.description)
                            .chartJsType(data.chartJsType)
                            .actif(true)
                            .build();

                    GrapheType savedType = grapheTypeService.create(grapheType);
                    log.info("Created graphe type: {} (ID: {})", savedType.getNom(), savedType.getId());
                } else {
                    log.info("Graphe type '{}' already exists. Skipping.", data.nom);
                }
            } catch (Exception e) {
                log.error("Error creating graphe type '{}': {}", data.nom, e.getMessage());
            }
        }
    }

    /**
     * Helper class to hold graphe type data
     */
    private static class GrapheTypeData {
        final String code;
        final String nom;
        final String description;
        final String chartJsType;

        GrapheTypeData(String code, String nom, String description, String chartJsType) {
            this.code = code;
            this.nom = nom;
            this.description = description;
            this.chartJsType = chartJsType;
        }
    }
}