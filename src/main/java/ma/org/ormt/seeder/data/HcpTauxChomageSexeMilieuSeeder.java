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
import ma.org.ormt.modules.hcp.chomage.sexe_milieu.SexeMilieu;
import ma.org.ormt.modules.hcp.chomage.sexe_milieu.service.SexeMilieuService;

@Component
@Order(2)
@RequiredArgsConstructor
public class HcpTauxChomageSexeMilieuSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    private final SexeMilieuService sexeMilieuService;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (!seeding)
            return;

        Resource resource = resourceLoader.getResource("classpath:init-data/hcp/taux_chomage_par_sexe_milieu.json");
        try (InputStream inputStream = resource.getInputStream()) {
            List<SexeMilieu> sexeMilieus = objectMapper.readValue(inputStream, new TypeReference<List<SexeMilieu>>() {
            });
            for (SexeMilieu sexeMilieu : sexeMilieus) {
                sexeMilieuService.create(sexeMilieu);
            }
        }
    }

}