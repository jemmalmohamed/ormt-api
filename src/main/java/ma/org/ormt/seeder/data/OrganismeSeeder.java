package ma.org.ormt.seeder.data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.organisme.Organisme;
import ma.org.ormt.modules.organisme.service.OrganismeService;

@Component
@Order(2)
@RequiredArgsConstructor
public class OrganismeSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private String seeding;

    private final OrganismeService organismeService;

    @Override
    public void run(String... args) throws Exception {
        if (!seeding.equals("true"))
            return;
        Organisme ancfcc = new Organisme();
        ancfcc.setNom("ancfcc");
        ancfcc.setSecteur("public");
        organismeService.create(ancfcc);

    }

}