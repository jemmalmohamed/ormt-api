package ma.org.ormt.seeder.data.domaine;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.domaine.Domaine;
import ma.org.ormt.modules.domaine.dto.request.DomaineRequestDto;
import ma.org.ormt.modules.domaine.service.DomaineService;
import ma.org.ormt.modules.sousdomaine.SousDomaine;
import ma.org.ormt.modules.sousdomaine.service.SousDomaineService;

@Component
@Order(2)
@RequiredArgsConstructor
public class DomaineSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    private final DomaineService domaineService;
    private final SousDomaineService sousDomaineService;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (!seeding)
            return;

        Resource domaineResource = resourceLoader.getResource("classpath:init-data/thematiques/domaines-data.json");
        try (InputStream inputStream = domaineResource.getInputStream()) {
            List<Domaine> domaines = objectMapper.readValue(inputStream,
                    new TypeReference<List<Domaine>>() {
                    });

            for (Domaine domaine : domaines) {
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

                    });
                }

            }
        }
    }

}