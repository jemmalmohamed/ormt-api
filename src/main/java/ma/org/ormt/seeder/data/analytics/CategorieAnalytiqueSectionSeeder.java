package ma.org.ormt.seeder.data.analytics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.analytics.category.dtos.request.CategorieAnalytiqueSectionRequestDto;
import ma.org.ormt.modules.analytics.domain.services.DomaineAnalytiqueService;

@Log4j2
@Component
@Order(14)
@RequiredArgsConstructor
public class CategorieAnalytiqueSectionSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    private final DomaineAnalytiqueService domaineAnalytiqueService;
    private final AnalyticsSeedJsonBuilder analyticsSeedJsonBuilder;

    @Override
    public void run(String... args) {
        if (!seeding) {
            return;
        }
        domaineAnalytiqueService.findAll().forEach(domain -> domaineAnalytiqueService
                .findCategoriesByDomaineAnalytique(domain.getId())
                .forEach(category -> {
                    try {
                        if (category.getTbdDashboard() == null
                                || (category.getSections() != null && !category.getSections().isEmpty())) {
                            return;
                        }
                        CategorieAnalytiqueSectionRequestDto requestDto = new CategorieAnalytiqueSectionRequestDto();
                        requestDto.setType("tbd_embed");
                        requestDto.setTitre(category.getLibelle());
                        requestDto.setContentJson(
                                analyticsSeedJsonBuilder.tbdEmbedContent(category.getTbdDashboard().getId()));
                        requestDto.setOrdre(0);
                        requestDto.setActif(true);
                        domaineAnalytiqueService.createCategorySection(category.getId(), requestDto);
                    } catch (Exception exception) {
                        log.error("Erreur de seed section catégorie analytique: {}", exception.getMessage(),
                                exception);
                    }
                }));
    }
}
