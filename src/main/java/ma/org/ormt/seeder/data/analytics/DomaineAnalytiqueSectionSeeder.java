package ma.org.ormt.seeder.data.analytics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.analytics.domain.dtos.request.DomaineAnalytiqueSectionRequestDto;
import ma.org.ormt.modules.analytics.domain.services.DomaineAnalytiqueService;

@Log4j2
@Component
@Order(13)
@RequiredArgsConstructor
public class DomaineAnalytiqueSectionSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    private final DomaineAnalytiqueService domaineAnalytiqueService;
    private final AnalyticsSeedJsonBuilder analyticsSeedJsonBuilder;

    @Override
    public void run(String... args) {
        if (!seeding) {
            return;
        }
        domaineAnalytiqueService.findAll().forEach(domain -> {
            try {
                if (domain.getSections() != null && !domain.getSections().isEmpty()) {
                    return;
                }
                if ((domain.getApropos() == null || domain.getApropos().isBlank())
                        && (domain.getImageUrl() == null || domain.getImageUrl().isBlank())) {
                    return;
                }
                DomaineAnalytiqueSectionRequestDto requestDto = new DomaineAnalytiqueSectionRequestDto();
                requestDto.setType("hero");
                requestDto.setTitre(domain.getTitre());
                requestDto.setContentJson(analyticsSeedJsonBuilder.heroContent(domain.getImageUrl(), domain.getApropos()));
                requestDto.setOrdre(0);
                requestDto.setActif(true);
                domaineAnalytiqueService.createSection(domain.getId(), requestDto);
            } catch (Exception exception) {
                log.error("Erreur de seed section hero domaine analytique: {}", exception.getMessage(), exception);
            }
        });
    }
}
