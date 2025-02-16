package ma.org.ormt.seeder.data.domaine;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.periodicite.Periodicite;
import ma.org.ormt.modules.periodicite.dto.request.PeriodiciteRequestDto;
import ma.org.ormt.modules.periodicite.service.PeriodiciteService;

@Component
@Order(2)
@RequiredArgsConstructor
public class PeriodiciteSeeder implements CommandLineRunner {

        @Value("${starter.database.seed}")
        private String seeding;

        private final PeriodiciteService periodiciteService;

        @Override
        public void run(String... args) throws Exception {

                if (!seeding.equals("true"))
                        return;

                List<PeriodiciteRequestDto> periodiciteList = Arrays.asList(

                                new PeriodiciteRequestDto("annuel", "Annuel"),
                                new PeriodiciteRequestDto("mensuel", "Mensuel"),
                                new PeriodiciteRequestDto("trimestriel", "Trimestriel"),
                                new PeriodiciteRequestDto("semestriel", "Semestriel"));

                // Save any remaining records that didn't make up a full batch
                if (!periodiciteList.isEmpty()) {
                        periodiciteList.forEach(periodicite -> {
                                Periodicite periodiciteEntity = Periodicite.builder().code(periodicite.getCode())
                                                .libelle(periodicite.getLibelle()).build();
                                periodiciteService.create(periodiciteEntity);
                        });

                }
        }

}