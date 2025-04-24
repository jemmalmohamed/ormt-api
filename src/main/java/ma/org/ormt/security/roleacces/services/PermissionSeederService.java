package ma.org.ormt.security.roleacces.services;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.org.ormt.modules.espaces.models.Espace;
import ma.org.ormt.modules.espaces.repositories.EspaceRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionSeederService implements CommandLineRunner {

    private final RoleAccesService roleAccesService;
    private final EspaceRepository espaceRepository;

    // Role code for public access - matches the one used in the controller
    private static final String PUBLIC_ROLE = "role_public";

    @Override
    @Transactional
    public void run(String... args) {
        seedPublicAccessForEspaces();
        log.info("Permission seeding completed");
    }

    /**
     * Seeds public access permissions for espaces
     * This makes resources accessible to unauthenticated users
     */
    public void seedPublicAccessForEspaces() {
        log.info("Seeding public access permissions for espaces...");

        // Get all espaces - you could filter this based on some criteria if needed
        List<Espace> allEspaces = espaceRepository.findAll();
        int count = 0;

        for (Espace espace : allEspaces) {
            // Check if access already exists to avoid duplicates
            if (!roleAccesService.hasAccess(PUBLIC_ROLE, "espace", espace.getId(), "lecture")) {
                // Add public read access
                roleAccesService.addAccess(PUBLIC_ROLE, "espace", espace.getId(), "lecture", "Auto-generated public access");
                count++;
            }
        }

        log.info("Added public read access to {} espaces", count);
    }

    /**
     * Method to manually trigger seeding (can be called from a controller if needed)
     */
    public void reseedAllPermissions() {
        seedPublicAccessForEspaces();
        // Add methods for other resource types as needed
    }
}