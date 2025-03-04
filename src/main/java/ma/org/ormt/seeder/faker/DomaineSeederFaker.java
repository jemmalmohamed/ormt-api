package ma.org.ormt.seeder.faker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.services.DomaineService;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.domaines.sousdomaine.services.SousDomaineService;

@Component
@Order(5)
@RequiredArgsConstructor
public class DomaineSeederFaker implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${starter.faker.seed}")
    private boolean seedingFaker;

    private final DomaineService domaineService;
    private final SousDomaineService sousDomaineService;

    Faker faker = new Faker(Locale.FRENCH);

    @Override
    public void run(String... args) throws Exception {
        if (!seeding)
            return;

        if (!seedingFaker)
            return;

        long totalDomaines = 1000;

        int batchSize = 5000;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        try {
            List<Future<Void>> futures = new ArrayList<>();

            for (int i = 0; i < totalDomaines; i += batchSize) {
                long finalI = i;
                futures.add(executor.submit(() -> {
                    createBatchTask(finalI, batchSize, totalDomaines);
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
        List<Domaine> domaineList = new ArrayList<>();
        long endIndex = Math.min(startIndex + batchSize, recordsNeeded);
        for (long i = startIndex; i < endIndex; i++) {
            Domaine domaine = new Domaine();
            domaine.setNom(faker.company().name().toLowerCase());
            domaine.setDescription(faker.lorem().sentence());

            domaineList.add(domaine);
        }
        domaineService.saveAll(domaineList);
        createSousDomaine(domaineList);
    }

    private void createSousDomaine(List<Domaine> domaineList) {
        // Create 10 SousDomaine for each Domaine
        for (Domaine domaine : domaineList) {
            List<SousDomaine> sousDomaineList = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                SousDomaine sousDomaine = new SousDomaine();
                sousDomaine.setNom(faker.company().name().toLowerCase());
                sousDomaine.setDescription(faker.lorem().sentence());
                sousDomaine.setDomaine(domaine);

                sousDomaineList.add(sousDomaine);
            }
            sousDomaineService.saveAll(sousDomaineList);
        }
    }

}