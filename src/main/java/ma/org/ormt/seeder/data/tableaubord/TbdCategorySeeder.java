package ma.org.ormt.seeder.data.tableaubord;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.dashboard.tbd.categories.services.TbdCategoryService;

@Log4j2
@Component
@Order(7)
@RequiredArgsConstructor
public class TbdCategorySeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    private final TbdCategoryService tbdCategoryService;

    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping TBD category sync.");
            return;
        }

        try {
            log.info("Syncing TBD categories from legacy TB domaine indicators...");
            tbdCategoryService.syncCategoriesFromDomaines();
            log.info("TBD category sync completed.");
        } catch (Exception e) {
            log.error("Error during TBD category sync: {}", e.getMessage(), e);
        }
    }
}
