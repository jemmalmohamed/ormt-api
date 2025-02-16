package ma.org.ormt.seeder.data.domaine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.domaine.Domaine;
import ma.org.ormt.modules.domaine.dto.request.DomaineRequestDto;
import ma.org.ormt.modules.domaine.service.DomaineService;
import ma.org.ormt.modules.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateur.models.IndicateurDimension;
import ma.org.ormt.modules.indicateur.service.IndicateurDimensionService;
import ma.org.ormt.modules.indicateur.service.IndicateurService;
import ma.org.ormt.modules.sousdomaine.SousDomaine;
import ma.org.ormt.modules.sousdomaine.service.SousDomaineService;

@Log4j2
@Component
@Order(3)
@RequiredArgsConstructor
public class DomaineSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    private final DomaineService domaineService;
    private final SousDomaineService sousDomaineService;
    private final IndicateurService indicateurService;
    private final IndicateurDimensionService indicateurDimensionService;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (!seeding)
            return;

        File folder = new File("src/main/resources/init-data/domaines");
        File[] listOfFiles = folder.listFiles((_, name) -> name.toLowerCase().endsWith(".json"));

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                processJsonFile(file);
            }
        }
    }

    private void processJsonFile(File file) throws IOException {
        try (InputStream inputStream = Files.newInputStream(Paths.get(file.getPath()))) {
            Domaine domaine = objectMapper.readValue(inputStream, Domaine.class);

            DomaineRequestDto requestDto = new DomaineRequestDto();
            requestDto.setTitre(domaine.getTitre());
            requestDto.setDescription(domaine.getDescription());
            Domaine createdDomaine = domaineService.create(requestDto);

            List<SousDomaine> sousDomaines = domaine.getSousDomaines();
            if (sousDomaines != null) {
                sousDomaines.forEach(sousDomaineRequest -> {
                    SousDomaine newSousDomaine = SousDomaine.builder()
                            .titre(sousDomaineRequest.getTitre())
                            .description(sousDomaineRequest.getDescription())
                            .domaine(createdDomaine)
                            .build();
                    sousDomaineService.create(newSousDomaine);

                    // create indicateurs
                    List<Indicateur> indicateursListRequest = sousDomaineRequest.getIndicateurs();
                    if (indicateursListRequest != null) {
                        indicateursListRequest.forEach(indicateurRequest -> {

                            Indicateur newIndicateur = new Indicateur();
                            newIndicateur.setNom(indicateurRequest.getNom());
                            newIndicateur.setDescription(indicateurRequest.getDescription());
                            newIndicateur.setAbreviation(indicateurRequest.getAbreviation());
                            newIndicateur.setTypeTb(indicateurRequest.getTypeTb());
                            newIndicateur.setUniteCalcul(indicateurRequest.getUniteCalcul());
                            newIndicateur.setSource(indicateurRequest.getSource());
                            newIndicateur.setRegleCalcul(indicateurRequest.getRegleCalcul());
                            newIndicateur.setCategorie(indicateurRequest.getCategorie());

                            newIndicateur.setSousDomaine(newSousDomaine);
                            log.info("indicateur: {}", indicateurRequest.getNom());
                            Indicateur createdIndicateur = indicateurService.create(newIndicateur);

                            // create dimensions
                            List<IndicateurDimension> dimensions = indicateurRequest.getDimensions();

                            if (dimensions != null) {
                                dimensions.forEach(dimension -> {
                                    IndicateurDimension dimensionRequestDto = new IndicateurDimension();
                                    dimensionRequestDto.setNom(dimension.getNom());
                                    dimensionRequestDto.setDescription(dimension.getDescription());
                                    dimensionRequestDto.setType("string");
                                    dimensionRequestDto.setIndicateur(createdIndicateur);
                                    indicateurDimensionService.create(dimensionRequestDto);
                                });
                            }
                        });
                    }

                });

            }

        }
    }

}