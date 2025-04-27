package ma.org.ormt.seeder.data.roleacces;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.espaces.repositories.EspaceRepository;
import ma.org.ormt.security.roleacces.services.RoleAccesService;

@Log4j2
@Component
@Order(5)
@RequiredArgsConstructor
public class RoleAccesSeederService implements CommandLineRunner {

    private final RoleAccesService roleAccesService;
    private final EspaceRepository espaceRepository;

    // Role code for public access
    private static final String PUBLIC_ROLE = "role_public";

    @Override
    @Transactional
    public void run(String... args) {
        // seedPublicAccessForEspaces();
    }

    /**
     * Seeds public access permissions for all espaces
     */
    public void seedPublicAccessForEspaces() {
        log.info("Seeding public access permissions for espaces...");

        // Get all espaces
        // List<Espace> allEspaces = espaceRepository.findAll();
        // int count = 0;

        // for (Espace espace : allEspaces) {
        // // Check if access already exists to avoid duplicates
        // if (!roleAccesService.hasAccess(PUBLIC_ROLE, "espace", espace.getId(),
        // "lecture")) {
        // // Add public read access
        // roleAccesService.addAccess(PUBLIC_ROLE, "espace", espace.getId(), "lecture",
        // "system");
        // count++;
        // }
        // }

        // log.info("Added public read access to {} espaces", count);
    }

    /**
     * Method to manually trigger seeding (can be called from a controller if
     * needed)
     */
    public void reseedAllPermissions() {
        // seedPublicAccessForEspaces();
        // Add other seeding methods as needed
    }
}