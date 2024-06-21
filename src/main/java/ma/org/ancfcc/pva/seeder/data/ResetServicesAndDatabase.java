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
        truncateTable("plan_action");
        // truncateMissionTable();
        // truncateOrganismeTable();
        // truncateBasemapTable();
        // truncateAvionTable();
        // truncateCapteurTable();
        // truncateBlocPrioriteTable();
        // truncateCarteTable();
    }

    private void truncateMissionTable() {
        entityManager.createNativeQuery("TRUNCATE TABLE mission RESTART IDENTITY CASCADE").executeUpdate();
        log.warn("### DATABASE: Mission table truncated successfully.");
    }

    private void truncateBasemapTable() {
        entityManager.createNativeQuery("TRUNCATE TABLE basemap RESTART IDENTITY CASCADE").executeUpdate();
        log.warn("### DATABASE: Basemap table truncated successfully.");

    }

    private void truncateCarteTable() {
        entityManager.createNativeQuery("TRUNCATE TABLE carte RESTART IDENTITY CASCADE").executeUpdate();
        log.warn("### DATABASE: Carte table truncated successfully.");

    }

    private void truncateBlocPrioriteTable() {
        entityManager.createNativeQuery("TRUNCATE TABLE bloc_priorite RESTART IDENTITY CASCADE").executeUpdate();
        log.warn("### DATABASE: Bloc priorite table truncated successfully.");

    }

    @Transactional
    public void truncateTable(String tableName) {
        entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        log.warn("### DATABASE: Plan action table truncated and identity reset successfully.");
    }

    private void truncateCapteurTable() {
        entityManager.createNativeQuery("TRUNCATE TABLE capteur RESTART IDENTITY CASCADE").executeUpdate();
        log.warn("### DATABASE: Capteur table truncated successfully.");

    }

    private void truncateAvionTable() {
        entityManager.createNativeQuery("TRUNCATE TABLE avion RESTART IDENTITY CASCADE").executeUpdate();
        log.warn("### DATABASE: Avion table truncated successfully.");

    }

    private void truncateOrganismeTable() {
        entityManager.createNativeQuery("TRUNCATE TABLE organisme RESTART IDENTITY CASCADE").executeUpdate();
        log.warn("### DATABASE: Organisme table truncated successfully.");

    }
}