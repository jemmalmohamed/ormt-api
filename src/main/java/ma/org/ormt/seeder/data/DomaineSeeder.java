package ma.org.ormt.seeder.data;

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
import ma.org.ormt.modules.domaine.service.DomaineService;

@Component
@Order(2)
@RequiredArgsConstructor
public class DomaineSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    private final DomaineService domaineService;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (!seeding)
            return;

        Resource resource = resourceLoader.getResource("classpath:init-data/thematiques/domaines.json");
        try (InputStream inputStream = resource.getInputStream()) {
            List<Domaine> domaines = objectMapper.readValue(inputStream,
                    new TypeReference<List<Domaine>>() {
                    });
            for (Domaine domaine : domaines) {
                domaineService.create(domaine);
            }
        }
    }

}