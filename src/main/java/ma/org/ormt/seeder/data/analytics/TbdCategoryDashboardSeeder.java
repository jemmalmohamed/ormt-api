package ma.org.ormt.seeder.data.analytics;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytique;
import ma.org.ormt.modules.analytics.category.repositories.CategorieAnalytiqueRepository;
import ma.org.ormt.modules.analytics.domain.DomaineAnalytiqueNamingService;
import ma.org.ormt.modules.analytics.domain.models.DomaineAnalytique;
import ma.org.ormt.modules.dashboard.tbd.models.TbdDashboard;
import ma.org.ormt.modules.dashboard.tbd.models.TbdSection;
import ma.org.ormt.modules.dashboard.tbd.models.TbdSourceListing;
import ma.org.ormt.modules.dashboard.tbd.models.TbdWidget;
import ma.org.ormt.modules.dashboard.tbd.models.TbdWidgetRow;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdDashboardRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdSectionRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdSourceListingRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdWidgetRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdWidgetRowRepository;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.repositories.DomaineRepository;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.domaines.sousdomaine.repositories.SousDomaineRepository;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.source.models.Source;

@Log4j2
@Component
@Order(12)
@RequiredArgsConstructor
public class TbdCategoryDashboardSeeder implements CommandLineRunner {

    private static final String SEED_MARKER = "seed:tbd-category-auto";
    private static final String SEED_SOURCE_TEXT = "Dashboard initial genere automatiquement pour categorie analytique.";
    private static final Comparator<SousDomaine> SOUS_DOMAINE_COMPARATOR = Comparator
            .comparing(TbdCategoryDashboardSeeder::safeInteger)
            .thenComparing(TbdCategoryDashboardSeeder::safeString);
    private static final Comparator<Indicateur> INDICATEUR_COMPARATOR = Comparator
            .comparing(TbdCategoryDashboardSeeder::indicatorSortLabel)
            .thenComparing(indicateur -> normalize(indicateur.getNom()));

    @Value("${starter.database.seed}")
    private boolean seeding;

    private final CategorieAnalytiqueRepository categorieAnalytiqueRepository;
    private final TbdDashboardRepository tbdDashboardRepository;
    private final TbdSectionRepository tbdSectionRepository;
    private final TbdWidgetRowRepository tbdWidgetRowRepository;
    private final TbdWidgetRepository tbdWidgetRepository;
    private final TbdSourceListingRepository tbdSourceListingRepository;
    private final DomaineRepository domaineRepository;
    private final SousDomaineRepository sousDomaineRepository;
    private final DomaineAnalytiqueNamingService namingService;
    private final AnalyticsSeedJsonBuilder analyticsSeedJsonBuilder;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) {
        if (!seeding) {
            return;
        }

        List<CategorieAnalytique> categories = categorieAnalytiqueRepository
                .findActiveWithoutDashboardOrderByDomainAndCategory();
        if (categories.isEmpty()) {
            log.info("Aucune categorie analytique active sans dashboard a seeder.");
            return;
        }

        Map<Long, Domaine> domainsById = domaineRepository.findAll().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Domaine::getId, domain -> domain, (left, _) -> left, LinkedHashMap::new));
        Map<String, Domaine> domainsByThemeKey = domainsById.values().stream()
                .filter(domain -> domain.getNom() != null)
                .collect(Collectors.toMap(
                        domain -> namingService.normalizeThemeKey(domain.getNom()),
                        domain -> domain,
                        (left, _) -> left,
                        LinkedHashMap::new));

        for (CategorieAnalytique category : categories) {
            try {
                seedCategoryDashboard(category, domainsById, domainsByThemeKey);
            } catch (Exception exception) {
                log.error("Erreur lors du seed du dashboard initial pour la categorie '{}': {}",
                        category.getLibelle(), exception.getMessage(), exception);
            }
        }
    }

    @Transactional
    protected void seedCategoryDashboard(
            CategorieAnalytique category,
            Map<Long, Domaine> domainsById,
            Map<String, Domaine> domainsByThemeKey) {
        if (category.getTbdDashboard() != null) {
            return;
        }

        String dashboardName = buildDashboardName(category);
        if (tbdDashboardRepository.findByNomIgnoreCaseAndActifTrue(dashboardName).isPresent()) {
            log.info("Dashboard '{}' deja present. Seed ignore pour la categorie '{}'.", dashboardName,
                    category.getLibelle());
            return;
        }

        Domaine canonicalDomain = resolveCanonicalDomain(category.getDomaineAnalytique(), domainsById,
                domainsByThemeKey)
                .orElse(null);
        if (canonicalDomain == null) {
            log.warn("Aucun domaine canonique resolu pour la categorie '{}'. Seed ignore.", category.getLibelle());
            return;
        }

        List<SousDomaineIndicators> groups = buildIndicatorGroups(canonicalDomain);
        if (groups.isEmpty()) {
            log.info("Aucun indicateur exploitable pour la categorie '{}' sur le domaine '{}'. Seed ignore.",
                    category.getLibelle(), canonicalDomain.getNom());
            return;
        }

        List<Indicateur> perimeterIndicators = groups.stream()
                .flatMap(group -> group.indicators().stream())
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Indicateur::getId, indicator -> indicator, (left, _) -> left,
                                LinkedHashMap::new),
                        map -> new ArrayList<>(map.values())));

        TbdDashboard dashboard = tbdDashboardRepository.save(TbdDashboard.builder()
                .nom(dashboardName)
                .titre(category.getLibelle())
                .sousTitre(category.getDomaineAnalytique().getTitre())
                .description(buildDashboardDescription(category))
                .sourceText(SEED_SOURCE_TEXT)
                .status("PUBLISHED")
                .actif(true)
                .createdBy(SEED_MARKER)
                .lastModifiedBy(SEED_MARKER)
                .build());

        createSourceListings(dashboard.getId(), perimeterIndicators);
        createKpiSection(dashboard.getId(), perimeterIndicators);
        createIndicatorSection(dashboard.getId(), groups);
        createEditorSection(dashboard.getId(), category, canonicalDomain);

        category.setTbdDashboard(dashboard);
        categorieAnalytiqueRepository.save(category);

        log.info("Dashboard initial '{}' cree et assigne a la categorie '{}'.", dashboard.getNom(),
                category.getLibelle());
    }

    private Optional<Domaine> resolveCanonicalDomain(
            DomaineAnalytique analyticDomain,
            Map<Long, Domaine> domainsById,
            Map<String, Domaine> domainsByThemeKey) {
        if (analyticDomain == null) {
            return Optional.empty();
        }

        Long canonicalDomainId = extractCanonicalDomainId(analyticDomain.getMetadataJson());
        if (canonicalDomainId != null && domainsById.containsKey(canonicalDomainId)) {
            return Optional.of(domainsById.get(canonicalDomainId));
        }

        String sourceThemeKey = namingService.normalizeThemeKey(analyticDomain.getSourceThemeKey());
        if (!sourceThemeKey.isBlank() && domainsByThemeKey.containsKey(sourceThemeKey)) {
            return Optional.of(domainsByThemeKey.get(sourceThemeKey));
        }

        String titleThemeKey = namingService.normalizeThemeKey(analyticDomain.getTitre());
        if (!titleThemeKey.isBlank() && domainsByThemeKey.containsKey(titleThemeKey)) {
            return Optional.of(domainsByThemeKey.get(titleThemeKey));
        }

        return Optional.empty();
    }

    private List<SousDomaineIndicators> buildIndicatorGroups(Domaine canonicalDomain) {
        return sousDomaineRepository.findByDomaineIdOrderByOrdreAsc(canonicalDomain.getId()).stream()
                .sorted(SOUS_DOMAINE_COMPARATOR)
                .map(sousDomaine -> new SousDomaineIndicators(
                        sousDomaine,
                        sousDomaine.getIndicateurs().stream()
                                .filter(Objects::nonNull)
                                .filter(indicator -> !Boolean.FALSE.equals(indicator.getActif()))
                                .sorted(INDICATEUR_COMPARATOR)
                                .collect(Collectors.collectingAndThen(
                                        Collectors.toMap(Indicateur::getId, indicator -> indicator, (left, _) -> left,
                                                LinkedHashMap::new),
                                        map -> new ArrayList<>(map.values())))))
                .filter(group -> !group.indicators().isEmpty())
                .collect(Collectors.toList());
    }

    private void createSourceListings(Long dashboardId, List<Indicateur> indicators) {
        int order = 1;
        Map<Long, Source> uniqueSources = new LinkedHashMap<>();
        for (Indicateur indicator : indicators) {
            Source source = indicator.getSource();
            if (source == null || source.getId() == null || uniqueSources.containsKey(source.getId())) {
                continue;
            }
            uniqueSources.put(source.getId(), source);
        }

        for (Source source : uniqueSources.values()) {
            tbdSourceListingRepository.save(TbdSourceListing.builder()
                    .dashboardId(dashboardId)
                    .source(source)
                    .ordre(order++)
                    .createdBy(SEED_MARKER)
                    .lastModifiedBy(SEED_MARKER)
                    .build());
        }
    }

    private void createKpiSection(Long dashboardId, List<Indicateur> perimeterIndicators) {
        TbdSection section = tbdSectionRepository.save(TbdSection.builder()
                .dashboardId(dashboardId)
                .label("Synthese")
                .ordre(1)
                .sizePercent(20)
                .actif(true)
                .createdBy(SEED_MARKER)
                .lastModifiedBy(SEED_MARKER)
                .build());

        TbdWidgetRow row = tbdWidgetRowRepository.save(TbdWidgetRow.builder()
                .sectionId(section.getId())
                .ordre(1)
                .sizePercent(100)
                .heightPx(200)
                .createdBy(SEED_MARKER)
                .lastModifiedBy(SEED_MARKER)
                .build());

        List<String> titles = topKpiTitles(perimeterIndicators);
        for (int index = 0; index < 4; index++) {
            tbdWidgetRepository.save(TbdWidget.builder()
                    .rowId(row.getId())
                    .type("KPI_CARD")
                    .titre(titles.get(index))
                    .ordre(index + 1)
                    .sizePercent(25)
                    .actif(true)
                    .createdBy(SEED_MARKER)
                    .lastModifiedBy(SEED_MARKER)
                    .build());
        }
    }

    private void createIndicatorSection(Long dashboardId, List<SousDomaineIndicators> groups) {
        TbdSection section = tbdSectionRepository.save(TbdSection.builder()
                .dashboardId(dashboardId)
                .label("Indicateurs")
                .ordre(2)
                .sizePercent(55)
                .actif(true)
                .createdBy(SEED_MARKER)
                .lastModifiedBy(SEED_MARKER)
                .build());

        int rowOrder = 1;
        for (SousDomaineIndicators group : groups) {
            List<Indicateur> indicators = group.indicators();
            for (int startIndex = 0; startIndex < indicators.size(); startIndex += 2) {
                List<Indicateur> chunk = indicators.subList(startIndex, Math.min(startIndex + 2, indicators.size()));
                TbdWidgetRow row = tbdWidgetRowRepository.save(TbdWidgetRow.builder()
                        .sectionId(section.getId())
                        .ordre(rowOrder++)
                        .sizePercent(100)
                        .heightPx(220)
                        .createdBy(SEED_MARKER)
                        .lastModifiedBy(SEED_MARKER)
                        .build());

                for (int widgetIndex = 0; widgetIndex < chunk.size(); widgetIndex++) {
                    Indicateur indicator = chunk.get(widgetIndex);
                    int widgetSize = chunk.size() == 1 ? 100 : 50;
                    String title = buildChartTitle(group.sousDomaine().getNom(), indicator);
                    tbdWidgetRepository.save(TbdWidget.builder()
                            .rowId(row.getId())
                            .type("CHART")
                            .titre(title)
                            .indicateur(indicator)
                            .ordre(widgetIndex + 1)
                            .sizePercent(widgetSize)
                            .actif(true)
                            .createdBy(SEED_MARKER)
                            .lastModifiedBy(SEED_MARKER)
                            .build());
                }
            }
        }
    }

    private void createEditorSection(Long dashboardId, CategorieAnalytique category, Domaine canonicalDomain) {
        TbdSection section = tbdSectionRepository.save(TbdSection.builder()
                .dashboardId(dashboardId)
                .label("Analyse et tendances")
                .ordre(3)
                .sizePercent(25)
                .actif(true)
                .createdBy(SEED_MARKER)
                .lastModifiedBy(SEED_MARKER)
                .build());

        TbdWidgetRow row = tbdWidgetRowRepository.save(TbdWidgetRow.builder()
                .sectionId(section.getId())
                .ordre(1)
                .sizePercent(100)
                .heightPx(240)
                .createdBy(SEED_MARKER)
                .lastModifiedBy(SEED_MARKER)
                .build());

        tbdWidgetRepository.save(TbdWidget.builder()
                .rowId(row.getId())
                .type("EDITOR")
                .titre("Analyse et tendances")
                .ordre(1)
                .sizePercent(100)
                .contentJson(buildEditorTemplate(category, canonicalDomain))
                .actif(true)
                .createdBy(SEED_MARKER)
                .lastModifiedBy(SEED_MARKER)
                .build());
    }

    private List<String> topKpiTitles(List<Indicateur> perimeterIndicators) {
        List<String> titles = perimeterIndicators.stream()
                .limit(4)
                .map(TbdCategoryDashboardSeeder::indicatorDisplayTitle)
                .collect(Collectors.toCollection(ArrayList::new));
        while (titles.size() < 4) {
            titles.add("KPI " + (titles.size() + 1) + " a completer");
        }
        return titles;
    }

    private String buildDashboardName(CategorieAnalytique category) {
        String base = hasText(category.getSlug()) ? category.getSlug() : category.getLibelle();
        String normalized = normalize(base);
        if (normalized.isBlank()) {
            normalized = "categorie-" + category.getId();
        }
        return normalized;
    }

    private String buildDashboardDescription(CategorieAnalytique category) {
        return "Dashboard initial seede automatiquement pour la categorie analytique '"
                + defaultString(category.getLibelle(), "Sans libelle") + "'.";
    }

    private String buildEditorTemplate(CategorieAnalytique category, Domaine canonicalDomain) {
        String html = """
                <p><strong>Analyse initiale</strong></p>
                <p>Cette zone est pre-remplie pour accompagner la categorie <em>%s</em> du domaine <em>%s</em>.</p>
                <ul>
                  <li>Ajouter ici les points saillants observes sur les indicateurs.</li>
                  <li>Documenter les evolutions marquantes et les tendances a surveiller.</li>
                  <li>Completer avec le contexte metier, les sources et les hypotheses utiles.</li>
                </ul>
                <p><em>Contenu a enrichir par les equipes metier avant publication editoriale finale.</em></p>
                """
                .formatted(
                        escapeHtml(defaultString(category.getLibelle(), "Categorie")),
                        escapeHtml(defaultString(canonicalDomain.getNom(), "Domaine")));
        return analyticsSeedJsonBuilder.editorContent(html, "#fcfbf8", "#1f2937");
    }

    private Long extractCanonicalDomainId(String metadataJson) {
        if (!hasText(metadataJson)) {
            return null;
        }
        try {
            Map<String, Object> metadata = objectMapper.readValue(metadataJson,
                    new TypeReference<Map<String, Object>>() {
                    });
            Object rawValue = metadata.get("canonicalDomaineId");
            if (rawValue instanceof Number numberValue) {
                return numberValue.longValue();
            }
            if (rawValue instanceof String stringValue && !stringValue.isBlank()) {
                return Long.parseLong(stringValue.trim());
            }
        } catch (Exception exception) {
            log.debug("Impossible de lire le metadataJson du domaine analytique: {}", exception.getMessage());
        }
        return null;
    }

    private String buildChartTitle(String sousDomaineName, Indicateur indicator) {
        if (!hasText(sousDomaineName)) {
            return indicatorDisplayTitle(indicator);
        }
        return sousDomaineName.trim() + " - " + indicatorDisplayTitle(indicator);
    }

    private static String indicatorDisplayTitle(Indicateur indicator) {
        if (indicator == null) {
            return "Indicateur";
        }
        if (hasText(indicator.getTitre())) {
            return indicator.getTitre().trim();
        }
        if (hasText(indicator.getNom())) {
            return indicator.getNom().trim();
        }
        return "Indicateur " + indicator.getId();
    }

    private static String indicatorSortLabel(Indicateur indicator) {
        return normalize(indicatorDisplayTitle(indicator));
    }

    private static String safeString(SousDomaine sousDomaine) {
        return normalize(sousDomaine == null ? null : sousDomaine.getNom());
    }

    private static Integer safeInteger(SousDomaine sousDomaine) {
        if (sousDomaine == null || sousDomaine.getOrdre() == null) {
            return Integer.MAX_VALUE;
        }
        return sousDomaine.getOrdre();
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String defaultString(String value, String fallback) {
        return hasText(value) ? value.trim() : fallback;
    }

    private static String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private record SousDomaineIndicators(SousDomaine sousDomaine, List<Indicateur> indicators) {
    }
}
