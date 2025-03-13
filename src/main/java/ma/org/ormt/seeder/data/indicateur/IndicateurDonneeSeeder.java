package ma.org.ormt.seeder.data.indicateur;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@Order(4)
@RequiredArgsConstructor
public class IndicateurDonneeSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Override
    public void run(String... args) throws Exception {
        if (seeding) {
            log.info("Seeding the database...");

        } else {
            log.info("Database seeding is skipped.");
        }
    }

}