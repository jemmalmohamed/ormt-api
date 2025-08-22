package ma.org.ormt.seeder.data.graphe;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
@Order(2)
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
        Instant start = Instant.now();

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

        // Load existing by code (case-insensitive handling by normalizing codes to
        // upper)
        Map<String, GrapheType> existingByCode = grapheTypeService
                .findByCodeIn(grapheTypesData.stream().map(gt -> gt.code).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(gt -> gt.getCode(), Function.identity()));

        int created = 0;
        int updated = 0;
        for (GrapheTypeData data : grapheTypesData) {
            try {
                GrapheType existing = existingByCode.get(data.code);
                if (existing == null) {
                    GrapheType grapheType = GrapheType.builder()
                            .code(data.code.toLowerCase())
                            .nom(data.nom)
                            .description(data.description)
                            .chartJsType(data.chartJsType)
                            .actif(true)
                            .build();
                    GrapheType savedType = grapheTypeService.create(grapheType);
                    log.info("Created graphe type: {} (code: {}, id: {})", savedType.getNom(), savedType.getCode(),
                            savedType.getId());
                    created++;
                } else {
                    boolean changed = false;
                    if (!equalsNullable(existing.getNom(), data.nom)) {
                        existing.setNom(data.nom);
                        changed = true;
                    }
                    if (!equalsNullable(existing.getDescription(), data.description)) {
                        existing.setDescription(data.description);
                        changed = true;
                    }
                    if (!equalsNullable(existing.getChartJsType(), data.chartJsType)) {
                        existing.setChartJsType(data.chartJsType);
                        changed = true;
                    }
                    if (existing.getActif() == null || !existing.getActif()) {
                        existing.setActif(true);
                        changed = true;
                    }
                    if (changed) {
                        grapheTypeService.update(existing.getId(), existing);
                        log.info("Updated graphe type: {} (code: {})", existing.getNom(), existing.getCode());
                        updated++;
                    } else {
                        log.debug("No changes for graphe type code {}", existing.getCode());
                    }
                }
            } catch (Exception e) {
                log.error("Error processing graphe type '{}': {}", data.code, e.getMessage(), e);
            }
        }

        Duration took = Duration.between(start, Instant.now());
        log.info("Graphe types seeding summary: created={}, updated={}, total={}, took={} ms", created, updated,
                grapheTypesData.size(), took.toMillis());
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

    private boolean equalsNullable(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }
}