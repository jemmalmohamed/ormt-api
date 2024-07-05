package ma.org.ancfcc.pva.seeder.faker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import lombok.RequiredArgsConstructor;
import ma.org.ancfcc.pva.modules.organisme.Organisme;
import ma.org.ancfcc.pva.modules.organisme.service.OrganismeService;

@Component
@Order(2)
@RequiredArgsConstructor
public class OrganismeSeederFaker implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private String seeding;

    @Value("${starter.faker.seed}")
    private String seedingFaker;

    private final OrganismeService organismeService;

    Faker faker = new Faker(Locale.FRENCH);

    @Override
    public void run(String... args) throws Exception {
        if (!seeding.equals("true"))
            return;

        if (!seedingFaker.equals("true"))
            return;

        long totalOrganismes = 20;

        int batchSize = 5000;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        try {
            List<Future<Void>> futures = new ArrayList<>();

            for (int i = 0; i < totalOrganismes; i += batchSize) {
                long finalI = i;
                futures.add(executor.submit(() -> {
                    createBatchTask(finalI, batchSize, totalOrganismes);
                    return null;
                }));
            }

            // Wait for all futures to complete
            for (Future<Void> future : futures) {
                future.get();
            }
        } finally {
            executor.shutdown();
        }

    }

    private void createBatchTask(long startIndex, int batchSize, long recordsNeeded) {
        List<Organisme> organismeList = new ArrayList<>();
        long endIndex = Math.min(startIndex + batchSize, recordsNeeded);
        for (long i = startIndex; i < endIndex; i++) {
            Organisme organisme = new Organisme();
            organisme.setNom(faker.company().name().toLowerCase());
            organisme.setSecteur(getRandomSector().toLowerCase());

            organismeList.add(organisme);
        }
        organismeService.saveAll(organismeList);
    }

    private static String getRandomSector() {
        String[] sectors = { "public", "privé" };
        int randomIndex = ThreadLocalRandom.current().nextInt(sectors.length);
        return sectors[randomIndex];
    }
}