package ma.org.ormt.seeder.data;

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
        truncateTable("province");
        truncateTable("region");

        truncateTable("domaine");
        truncateTable("sous_domaine");

        truncateTable("espace_domaine");
        truncateTable("espace");
        truncateTable("source");

        truncateTable("indicateur_dimension");
        truncateTable("dimension");
        truncateTable("indicateur");
    }

    @Transactional
    public void truncateTable(String tableName) {
        String query = "TRUNCATE TABLE " + tableName + " RESTART IDENTITY CASCADE";
        entityManager.createNativeQuery(query).executeUpdate();
        log.warn("### DATABASE: Table '{}' truncated successfully.", tableName);
    }
}