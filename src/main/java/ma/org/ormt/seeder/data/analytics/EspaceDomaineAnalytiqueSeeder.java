package ma.org.ormt.seeder.data.analytics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.analytics.domain.DomaineAnalytiqueNamingService;
import ma.org.ormt.modules.analytics.domain.dtos.request.DomaineAnalytiqueLinkRequestDto;
import ma.org.ormt.modules.analytics.domain.services.DomaineAnalytiqueService;
import ma.org.ormt.modules.espaces.association.domaine.EspaceDomaine;
import ma.org.ormt.modules.espaces.association.domaine.repository.EspaceDomaineRepository;

@Log4j2
@Component
@Order(10)
@RequiredArgsConstructor
public class EspaceDomaineAnalytiqueSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    private final EspaceDomaineRepository espaceDomaineRepository;
    private final DomaineAnalytiqueService domaineAnalytiqueService;
    private final DomaineAnalytiqueNamingService namingService;

    @Override
    public void run(String... args) {
        if (!seeding) {
            return;
        }
        for (EspaceDomaine espaceDomaine : espaceDomaineRepository.findAll()) {
            try {
                if (espaceDomaine.getEspace() == null || espaceDomaine.getDomaine() == null) {
                    continue;
                }
                String themeKey = namingService.normalizeThemeKey(espaceDomaine.getDomaine().getNom());
                var domaineAnalytique = domaineAnalytiqueService.findBySourceThemeKey(themeKey).orElse(null);
                if (domaineAnalytique == null) {
                    continue;
                }
                domaineAnalytiqueService.attachToEspace(espaceDomaine.getEspace().getId(),
                        new DomaineAnalytiqueLinkRequestDto(domaineAnalytique.getId(), espaceDomaine.getOrdre()));
            } catch (Exception exception) {
                log.error("Erreur de seed association espace/domaine-analytique: {}", exception.getMessage(),
                        exception);
            }
        }
    }
}
