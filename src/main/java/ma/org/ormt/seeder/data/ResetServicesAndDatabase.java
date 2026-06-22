package ma.org.ormt.seeder.data;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
@Order(1)
public class ResetServicesAndDatabase implements CommandLineRunner {

    private static final List<String> TABLES_TO_TRUNCATE = List.of(
            "categorie_analytique_section",
            "domaine_analytique_section",
            "tb_group_domaine_analytique",
            "espace_domaine_analytique",
            "categorie_analytique",
            "domaine_analytique",
            "tbd_widget",
            "tbd_widget_row",
            "tbd_section",
            "tbd_source_listing",
            "tbd_dashboard",
            "observatoire_page_content",
            "tb_group",
            "graphe_configuration",
            "graphe_type",
            "indicateur_dimension",
            "indicateur",
            "dimension",
            "source",
            "publication",
            "partenaire",
            "espace",
            "sous_domaine",
            "domaine",
            "province",
            "region",
            "role_acces");

    @Value("${starter.database.reset}")
    private boolean resetDatabase;

    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        if (!resetDatabase) {
            log.warn("### DATABASE: Skipping database reset");
            return;
        }

        log.warn("### DATABASE: Resetting database...");
        resetDatabase();
        log.warn("### DATABASE: Database reset successfully.");
    }

    private void resetDatabase() {
        TABLES_TO_TRUNCATE.forEach(this::truncateTable);
    }

    @Transactional
    public void truncateTable(String tableName) {
        String query = "TRUNCATE TABLE " + tableName + " RESTART IDENTITY CASCADE";
        entityManager.createNativeQuery(query).executeUpdate();
        log.warn("### DATABASE: Table '{}' truncated successfully.", tableName);
    }
}
