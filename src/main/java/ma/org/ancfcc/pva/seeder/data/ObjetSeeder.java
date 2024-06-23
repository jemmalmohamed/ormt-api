package ma.org.ancfcc.pva.seeder.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ma.org.ancfcc.pva.modules.objet.Objet;
import ma.org.ancfcc.pva.modules.objet.service.ObjetService;

@Component
@Order(2)
@RequiredArgsConstructor
public class ObjetSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private String seeding;

    private final ObjetService objetService;

    @Override
    public void run(String... args) throws Exception {
        if (!seeding.equals("true"))
            return;

        List<String> projectNames = new ArrayList<>();
        projectNames.add("ife");
        projectNames.add("màj couverture aérienne du 50k à une échelle de 1/40000");
        projectNames.add("màj couverture aérienne des villes");
        projectNames.add("màj couverture aérienne à une résolution de 35/40 cm");
        projectNames.add("màj couverture aérienne à une résolution de 50/60cm");
        projectNames.add("màj couverture aérienne du 25k à une résolution de 40cm");
        projectNames.add("màj couverture aérienne du 50k à une résolution de 60cm");
        projectNames.add("màj couverture aérienne du littoral sud à une résolution de 40cm");
        projectNames.add("màj couverture aérienne du 50k à une résolution de 50cm");
        projectNames.add("màj couverture aérienne du 25k à une résolution de 50cm");
        projectNames.add("màj couverture aérienne du 50k à une résolution de 40cm");
        projectNames.add("établissement plan de ville");
        projectNames.add("cartographie de base");
        projectNames.add("projet séisme");
        projectNames.add("ife 2023");
        projectNames.add("ife 2024");
        projectNames.add("Besoin Carto 2023");
        projectNames.add("Besoin Carto 2024");
        projectNames.add("Besoin Carto 2025");
        projectNames.add("Besoin Carto 2026");
        projectNames.add("établissement Cartes 50k");

        List<Objet> objets = new ArrayList<>();
        for (String projectName : projectNames) {
            Objet objet = new Objet();
            objet.setNom(projectName.toLowerCase());
            objet.setDescription(projectName.toLowerCase());
            objets.add(objet);
        }

        for (Objet objet : objets) {
            objetService.create(objet);
        }
    }

}