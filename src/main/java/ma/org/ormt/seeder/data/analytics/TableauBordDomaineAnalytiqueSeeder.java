package ma.org.ormt.seeder.data.analytics;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.analytics.domain.DomaineAnalytiqueNamingService;
import ma.org.ormt.modules.analytics.domain.dtos.request.DomaineAnalytiqueLinkRequestDto;
import ma.org.ormt.modules.analytics.domain.dtos.request.DomaineAnalytiqueRequestDto;
import ma.org.ormt.modules.analytics.domain.services.DomaineAnalytiqueService;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.TableauBordDomaine;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.repository.TableauBordDomaineRepository;

@Log4j2
@Component
@Order(11)
@RequiredArgsConstructor
public class TableauBordDomaineAnalytiqueSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    private final TableauBordDomaineRepository tableauBordDomaineRepository;
    private final DomaineAnalytiqueService domaineAnalytiqueService;
    private final DomaineAnalytiqueNamingService namingService;
    private final AnalyticsSeedJsonBuilder analyticsSeedJsonBuilder;

    @Override
    public void run(String... args) {
        if (!seeding) {
            return;
        }
        for (TableauBordDomaine tableauBordDomaine : tableauBordDomaineRepository.findAll()) {
            try {
                if (tableauBordDomaine.getTableauBord() == null || tableauBordDomaine.getTbDomaine() == null) {
                    continue;
                }
                String themeSource = tableauBordDomaine.getTbDomaine().getLibelle() != null
                        ? tableauBordDomaine.getTbDomaine().getLibelle()
                        : tableauBordDomaine.getTbDomaine().getNom();
                String themeKey = namingService.normalizeThemeKey(themeSource);
                var domain = domaineAnalytiqueService.findBySourceThemeKey(themeKey)
                        .orElseGet(() -> {
                            DomaineAnalytiqueRequestDto requestDto = new DomaineAnalytiqueRequestDto();
                            requestDto.setNom(themeKey);
                            requestDto.setTitre(themeSource);
                            requestDto.setDescription(tableauBordDomaine.getTbDomaine().getDescription());
                            requestDto.setSlug(themeKey);
                            requestDto.setSourceThemeKey(themeKey);
                            requestDto.setActif(tableauBordDomaine.getTbDomaine().getActif() == null
                                    ? true
                                    : tableauBordDomaine.getTbDomaine().getActif());
                            requestDto.setMetadataJson(analyticsSeedJsonBuilder.metadata("tb_domaine", Map.of(
                                    "sourceThemeKey", themeKey,
                                    "legacyTbDomaineId", tableauBordDomaine.getTbDomaine().getId(),
                                    "legacyTbDomaineNom", tableauBordDomaine.getTbDomaine().getNom())));
                            return domaineAnalytiqueService.create(requestDto);
                        });
                domaineAnalytiqueService.attachToTbGroup(tableauBordDomaine.getTableauBord().getId(),
                        new DomaineAnalytiqueLinkRequestDto(domain.getId(), tableauBordDomaine.getOrdre()));
            } catch (Exception exception) {
                log.error("Erreur de seed association tb_group/domaine-analytique: {}", exception.getMessage(),
                        exception);
            }
        }
    }
}
