package ma.org.ormt.seeder.data.tableaubord;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.dashboard.tableaubord.v2.services.TableauBordV2Service;

@Log4j2
@Component
@Order(7)
@RequiredArgsConstructor
public class TableauBordV2CategorySeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    private final TableauBordV2Service tableauBordV2Service;

    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping TB V2 category sync.");
            return;
        }

        try {
            log.info("Syncing TB V2 categories from legacy TB domaine indicators...");
            tableauBordV2Service.syncCategoriesFromLegacyDomaines();
            log.info("TB V2 category sync completed.");
        } catch (Exception e) {
            log.error("Error during TB V2 category sync: {}", e.getMessage(), e);
        }
    }
}
