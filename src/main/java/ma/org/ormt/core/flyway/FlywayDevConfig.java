package ma.org.ormt.core.flyway;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
@Profile("dev")
public class FlywayDevConfig {

    @Bean
    public FlywayMigrationStrategy cleanMigrateStrategy() {
        log.warn("Running Flyway in dev mode with clean and migrate strategy");
        return flyway -> {
            try {
                log.warn(!flyway.getConfiguration().isCleanDisabled() ? "Flyway clean is enabled"
                        : "Flyway clean is disabled");
                if (!flyway.getConfiguration().isCleanDisabled()) {
                    flyway.clean();
                }

                // First try to repair to handle checksum mismatches
                flyway.repair();
                // Migrate before validate since we need to apply migrations first
                flyway.migrate();
                // Then validate
                flyway.validate();
            } catch (Exception e) {
                // If migration or validation fails after repair, clean and try again
                if (!flyway.getConfiguration().isCleanDisabled()) {
                    flyway.clean();
                    flyway.migrate();
                } else {
                    throw e;
                }
            }
        };
    }
}