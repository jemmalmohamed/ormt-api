package ma.org.ormt.seeder.data.tableaubord;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.TBDomaineDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.request.TBDomaineRequestDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.services.TBDomaineService;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos.request.TBDomaineIndicateurRequestDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.service.TBDomaineIndicateurService;
import ma.org.ormt.modules.indicateurs.graphe.configuration.models.GrapheConfiguration;
import ma.org.ormt.modules.indicateurs.graphe.configuration.repositories.GrapheConfigurationRepository;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.IndicateurDto;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Log4j2
@Component
@Order(5)
@RequiredArgsConstructor
public class TBDomaineTestSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    private final TBDomaineService tbDomaineService;
    private final TBDomaineIndicateurService tbDomaineIndicateurService;
    private final GrapheConfigurationRepository grapheConfigurationRepository;

    /**
     * Executes the TB domain data seeding process when the application starts.
     * Only runs if seeding is enabled in the configuration.
     *
     * @param args Command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping TB domaines data seeding.");
            return;
        }

        try {
            // Create separate TBDomaines for each chart type
            createTBDomainesForEachChartType();

            log.info("TB domaines test data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during TB domaines test data seeding: {}", e.getMessage(), e);
        }
    }

    /**
     * Creates separate TBDomaines for each chart type with all corresponding
     * indicators
     */
    @Transactional
    private void createTBDomainesForEachChartType() {
        // Chart types with their corresponding domain names
        String[][] chartTypeConfigs = {
                { "CAMEMBERT", "camembert", "Domaine Camembert",
                        "Domaine contenant tous les indicateurs avec graphique en camembert" },
                { "HISTOGRAMME", "bar", "Domaine Bar",
                        "Domaine contenant tous les indicateurs avec graphique en barres" },
                { "COURBES", "courbes", "Domaine Courbes",
                        "Domaine contenant tous les indicateurs avec graphique en courbes" },
                { "CHOROPLETH", "choropleth", "Domaine Choropleth",
                        "Domaine contenant tous les indicateurs avec graphique choropleth" }
        };

        for (String[] config : chartTypeConfigs) {
            String grapheTypeCode = config[0];
            String domaineName = config[1];
            String libelle = config[2];
            String description = config[3];

            try {
                createTBDomaineForChartType(grapheTypeCode, domaineName, libelle, description);
            } catch (Exception e) {
                log.error("Error creating TB domaine for chart type {}: {}", grapheTypeCode, e.getMessage(), e);
            }
        }
    }

    /**
     * Creates a TBDomaine for a specific chart type and assigns all indicators with
     * that default chart type
     */
    @Transactional
    private void createTBDomaineForChartType(String grapheTypeCode, String domaineName, String libelle,
            String description) {
        try {
            // Check if domain already exists
            if (tbDomaineService.findByNom(domaineName).isPresent()) {
                log.info("TB domaine '{}' already exists. Skipping creation.", domaineName);
                return;
            }

            // Create the TBDomaine
            TBDomaineRequestDto requestDto = new TBDomaineRequestDto();
            requestDto.setNom(domaineName);
            requestDto.setLibelle(libelle);
            requestDto.setDescription(description);
            requestDto.setActif(true);

            TBDomaine domaine = tbDomaineService.create(requestDto);
            log.info("Created TB domaine: {} (id={})", domaine.getNom(), domaine.getId());

            // Find all indicators with this default chart type
            List<GrapheConfiguration> configurations = grapheConfigurationRepository
                    .findAll()
                    .stream()
                    .filter(gc -> gc.getGrapheType().getCode().equalsIgnoreCase(grapheTypeCode) &&
                            Boolean.TRUE.equals(gc.getIsDefault()))
                    .toList();

            log.info("Found {} indicators with default {} chart type", configurations.size(), grapheTypeCode);

            // Associate all indicators of this chart type to the domain
            int ordre = 1;
            for (GrapheConfiguration config : configurations) {
                try {
                    Indicateur indicateur = config.getIndicateur();

                    // Create association request
                    TBDomaineIndicateurRequestDto associationRequest = new TBDomaineIndicateurRequestDto();

                    TBDomaineDto tbDomaineDto = new TBDomaineDto();
                    tbDomaineDto.setId(domaine.getId());
                    associationRequest.setTbDomaine(tbDomaineDto);

                    IndicateurDto indicateurDto = new IndicateurDto();
                    indicateurDto.setId(indicateur.getId());
                    associationRequest.setIndicateur(indicateurDto);

                    associationRequest.setCategorie(grapheTypeCode.toLowerCase());
                    associationRequest.setOrdre(ordre++);

                    // Create the association
                    tbDomaineIndicateurService.attachIndicateursToTBDomaine(Arrays.asList(associationRequest));

                    log.info("Associated indicator '{}' to {} domain", indicateur.getNom(), domaineName);

                } catch (Exception e) {
                    log.error("Error associating indicator to {} domain: {}", domaineName, e.getMessage());
                }
            }

            log.info("Completed creation of {} domain with {} indicators", domaineName, configurations.size());

        } catch (Exception e) {
            log.error("Error creating {} domain: {}", domaineName, e.getMessage(), e);
        }
    }
}
