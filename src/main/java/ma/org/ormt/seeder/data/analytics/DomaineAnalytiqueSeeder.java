package ma.org.ormt.seeder.data.analytics;

import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.analytics.domain.DomaineAnalytiqueNamingService;
import ma.org.ormt.modules.analytics.domain.dtos.request.DomaineAnalytiqueRequestDto;
import ma.org.ormt.modules.analytics.domain.services.DomaineAnalytiqueService;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.repositories.DomaineRepository;

@Log4j2
@Component
@Order(9)
@RequiredArgsConstructor
public class DomaineAnalytiqueSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${starter.database.seed-analytics-transition:false}")
    private boolean analyticsTransitionSeeding;

    private final DomaineRepository domaineRepository;
    private final DomaineAnalytiqueService domaineAnalytiqueService;
    private final DomaineAnalytiqueNamingService namingService;
    private final AnalyticsSeedJsonBuilder analyticsSeedJsonBuilder;

    @Override
    public void run(String... args) {
        if (!seeding || !analyticsTransitionSeeding) {
            return;
        }
        List<Domaine> domaines = domaineRepository.findAll();
        for (Domaine domaine : domaines) {
            try {
                String themeKey = namingService.normalizeThemeKey(domaine.getNom());
                if (themeKey.isBlank() || domaineAnalytiqueService.findBySourceThemeKey(themeKey).isPresent()) {
                    continue;
                }
                DomaineAnalytiqueRequestDto requestDto = new DomaineAnalytiqueRequestDto();
                requestDto.setNom(themeKey);
                requestDto.setTitre(domaine.getNom());
                requestDto.setDescription(domaine.getDescription());
                requestDto.setSlug(themeKey);
                requestDto.setSourceThemeKey(themeKey);
                requestDto.setActif(domaine.getActif() == null ? true : domaine.getActif());
                requestDto.setMetadataJson(analyticsSeedJsonBuilder.metadata("domaine", Map.of(
                        "sourceThemeKey", themeKey,
                        "canonicalDomaineId", domaine.getId(),
                        "canonicalDomaineNom", domaine.getNom())));
                domaineAnalytiqueService.create(requestDto);
            } catch (Exception exception) {
                log.error("Erreur de seed domaine-analytique pour le domaine '{}': {}", domaine.getNom(),
                        exception.getMessage(), exception);
            }
        }
    }
}
