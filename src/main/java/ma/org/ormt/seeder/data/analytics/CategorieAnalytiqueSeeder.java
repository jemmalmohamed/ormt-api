package ma.org.ormt.seeder.data.analytics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.analytics.category.dtos.request.CategorieAnalytiqueRequestDto;
import ma.org.ormt.modules.analytics.domain.DomaineAnalytiqueNamingService;
import ma.org.ormt.modules.analytics.domain.services.DomaineAnalytiqueService;
import ma.org.ormt.modules.dashboard.tbd.models.TbdAssignation;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdAssignationRepository;
import ma.org.ormt.modules.dashboard.tbd.categories.models.TbdCategory;
import ma.org.ormt.modules.dashboard.tbd.categories.repositories.TbdCategoryRepository;

@Log4j2
@Component
@Order(12)
@RequiredArgsConstructor
public class CategorieAnalytiqueSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    private final TbdCategoryRepository tbdCategoryRepository;
    private final TbdAssignationRepository tbdAssignationRepository;
    private final DomaineAnalytiqueService domaineAnalytiqueService;
    private final DomaineAnalytiqueNamingService namingService;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seeding) {
            return;
        }
        for (TbdCategory category : tbdCategoryRepository.findAll()) {
            try {
                if (category.getTbDomaine() == null) {
                    continue;
                }
                String label = category.getLibelle() != null && !category.getLibelle().isBlank()
                        ? category.getLibelle()
                        : category.getNom();
                if (label == null || label.isBlank()) {
                    continue;
                }
                String themeSource = category.getTbDomaine().getLibelle() != null
                        ? category.getTbDomaine().getLibelle()
                        : category.getTbDomaine().getNom();
                String themeKey = namingService.normalizeThemeKey(themeSource);
                var domain = domaineAnalytiqueService.findBySourceThemeKey(themeKey).orElse(null);
                if (domain == null) {
                    continue;
                }
                boolean exists = domaineAnalytiqueService.findCategoriesByDomaineAnalytique(domain.getId()).stream()
                        .anyMatch(item -> item.getNom().equalsIgnoreCase(namingService.normalizeSlug(label)));
                if (exists) {
                    continue;
                }
                TbdAssignation assignation = tbdAssignationRepository.findByCibleTypeAndCibleId("CATEGORIE", category.getId())
                        .orElse(null);
                CategorieAnalytiqueRequestDto requestDto = new CategorieAnalytiqueRequestDto();
                requestDto.setDomaineAnalytiqueId(domain.getId());
                requestDto.setNom(namingService.normalizeSlug(label));
                requestDto.setLibelle(label);
                requestDto.setDescription(category.getDescription());
                requestDto.setSlug(namingService.normalizeSlug(label));
                requestDto.setOrdre(category.getOrdre());
                requestDto.setActif(category.getActif() == null ? true : category.getActif());
                requestDto.setTbdDashboardId(assignation != null ? assignation.getDashboardId() : null);
                domaineAnalytiqueService.createCategory(requestDto);
            } catch (Exception exception) {
                log.error("Erreur de seed catégorie analytique: {}", exception.getMessage(), exception);
            }
        }
    }
}
