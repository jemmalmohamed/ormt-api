package ma.org.ancfcc.pva.seeder.data;

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
    private String resetDatabase;

    @Value("${starter.database.seed}")
    private String seedDatabase;

    @Value("${starter.geoserver.reset}")
    private String resetGeoserver;

    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        if (!resetDatabase.equals("true")) {
            log.warn("### DATABASE: Skipping database reset");
            return;
        }

        log.warn("### DATABASE: Resetting database...");
        resetDatabase();
        log.warn("### DATABASE: Database reset successfully.");
    }

    private void resetDatabase() {
        truncateTableIfExists("plan_action");
        truncateTableIfExists("organisme");
        truncateTableIfExists("avion");
        truncateTableIfExists("capteur");
        truncateTableIfExists("objet");
    }

    @Transactional
    public void truncateTableIfExists(String tableName) {
        String checkTableExistenceQuery = "IF EXISTS (SELECT 1 FROM sys.tables WHERE name = :tableName) " +
                "BEGIN " +
                "TRUNCATE TABLE " + tableName + "; " +
                "END";
        entityManager.createNativeQuery(checkTableExistenceQuery)
                .setParameter("tableName", tableName)
                .executeUpdate();
        log.warn("### DATABASE: Checked and truncated table if exists: " + tableName);
    }

}