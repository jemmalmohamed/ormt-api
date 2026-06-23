package ma.org.ormt.seeder.data.analytics;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
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
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytique;
import ma.org.ormt.modules.analytics.category.repositories.CategorieAnalytiqueRepository;
import ma.org.ormt.modules.analytics.domain.DomaineAnalytiqueNamingService;
import ma.org.ormt.modules.analytics.domain.models.DomaineAnalytique;
import ma.org.ormt.modules.chiffres.dtos.request.ChiffreCleRequestDto;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.chiffres.models.enums.KpiEvolutionMode;
import ma.org.ormt.modules.chiffres.models.enums.KpiFormatType;
import ma.org.ormt.modules.chiffres.models.enums.KpiModeSource;
import ma.org.ormt.modules.chiffres.services.ChiffreCleService;
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
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;
import ma.org.ormt.modules.indicateurs.source.models.Source;
import ma.org.ormt.modules.indicateurs.valeurdimension.models.ValeurDimension;

@Log4j2
@Component
@Order(12)
@RequiredArgsConstructor
public class TbdCategoryDashboardSeeder implements CommandLineRunner {

    private static final String SEED_MARKER = "seed:tbd-category-auto";
    private static final String SEED_SOURCE_TEXT = "Dashboard initial genere automatiquement pour categorie analytique.";
    private static final int KPI_ROW_HEIGHT_PX = 90;
    private static final int KPI_SECONDARY_ROW_HEIGHT_PX = 90;
    private static final int CHART_ROW_HEIGHT_PX = 290;
    private static final int INLINE_EDITOR_ROW_HEIGHT_PX = 170;
    private static final int SECTION_EDITOR_ROW_HEIGHT_PX = 210;
    private static final SeedKpiStylePalette[] KPI_SEED_STYLES = new SeedKpiStylePalette[] {
            new SeedKpiStylePalette("#e74c3c", "#ffffff", "#cf3f31", "0 10px 24px rgba(231, 76, 60, 0.18)"),
            new SeedKpiStylePalette("#39b563", "#ffffff", "#2f9953", "0 10px 24px rgba(57, 181, 99, 0.18)"),
            new SeedKpiStylePalette("#9b59b6", "#ffffff", "#854aa0", "0 10px 24px rgba(155, 89, 182, 0.18)"),
            new SeedKpiStylePalette("#f5a623", "#ffffff", "#dc9219", "0 10px 24px rgba(245, 166, 35, 0.18)")
    };
    private static final SeedKpiStylePalette[] KPI_SECONDARY_SEED_STYLES = new SeedKpiStylePalette[] {
            new SeedKpiStylePalette("#f3f4f6", "#1f3a5a", "#ef4444", "0 8px 20px rgba(148, 163, 184, 0.12)", 0, 0, 0, 4),
            new SeedKpiStylePalette("#f3f4f6", "#1f3a5a", "#22c55e", "0 8px 20px rgba(148, 163, 184, 0.12)", 0, 0, 0, 4),
            new SeedKpiStylePalette("#f3f4f6", "#1f3a5a", "#3b82f6", "0 8px 20px rgba(148, 163, 184, 0.12)", 0, 0, 0, 4),
            new SeedKpiStylePalette("#f3f4f6", "#1f3a5a", "#8b5cf6", "0 8px 20px rgba(148, 163, 184, 0.12)", 0, 0, 0, 4)
    };
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
    private final ChiffreCleService chiffreCleService;
    private final IndicateurService indicateurService;
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
        createIndicatorSection(dashboard.getId(), groups, perimeterIndicators);
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
                                .map(this::hydrateIndicatorForSeed)
                                .filter(this::hasSeedableIndicatorData)
                                .filter(Objects::nonNull)
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
                .heightPx(KPI_ROW_HEIGHT_PX)
                .createdBy(SEED_MARKER)
                .lastModifiedBy(SEED_MARKER)
                .build());

        List<KpiSeedCandidate> candidates = selectTopKpiCandidates(perimeterIndicators);
        for (int index = 0; index < 4; index++) {
            KpiSeedCandidate candidate = candidates.size() > index ? candidates.get(index) : null;
            if (candidate == null) {
                createEmptyWidget(row.getId(), index + 1, 25, "KPI indisponible");
                continue;
            }

            ChiffreCle seededKpi = createOrReuseIndicatorKpi(candidate, index, false);
            tbdWidgetRepository.save(TbdWidget.builder()
                    .rowId(row.getId())
                    .type("KPI_CARD")
                    .titre(indicatorDisplayTitle(candidate.indicator()))
                    .ordre(index + 1)
                    .sizePercent(25)
                    .kpiId(seededKpi.getId())
                    .actif(true)
                    .createdBy(SEED_MARKER)
                    .lastModifiedBy(SEED_MARKER)
                    .build());
        }
    }

    private void createIndicatorSection(Long dashboardId, List<SousDomaineIndicators> groups, List<Indicateur> perimeterIndicators) {
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
        boolean shouldInsertSecondaryKpis = perimeterIndicators != null && perimeterIndicators.size() > 8;
        boolean secondaryKpisInserted = false;
        for (SousDomaineIndicators group : groups) {
            List<Indicateur> indicators = group.indicators();
            int groupRowCount = 0;
            for (int startIndex = 0; startIndex < indicators.size(); startIndex += 2) {
                List<Indicateur> chunk = indicators.subList(startIndex, Math.min(startIndex + 2, indicators.size()));
                TbdWidgetRow row = tbdWidgetRowRepository.save(TbdWidgetRow.builder()
                        .sectionId(section.getId())
                        .ordre(rowOrder++)
                        .sizePercent(100)
                        .heightPx(CHART_ROW_HEIGHT_PX)
                        .createdBy(SEED_MARKER)
                        .lastModifiedBy(SEED_MARKER)
                        .build());

                if (chunk.size() == 1) {
                    createEmptyWidget(row.getId(), 1, 20, "Marge gauche");
                    createChartWidget(row.getId(), chunk.get(0), buildChartTitle(group.sousDomaine().getNom(), chunk.get(0)), 2, 60);
                    createEmptyWidget(row.getId(), 3, 20, "Marge droite");
                } else {
                    for (int widgetIndex = 0; widgetIndex < chunk.size(); widgetIndex++) {
                        Indicateur indicator = chunk.get(widgetIndex);
                        createChartWidget(
                                row.getId(),
                                indicator,
                                buildChartTitle(group.sousDomaine().getNom(), indicator),
                                widgetIndex + 1,
                                50);
                    }
                }

                if (shouldInsertSecondaryKpis && !secondaryKpisInserted) {
                    createSecondaryKpiRow(section.getId(), rowOrder++, perimeterIndicators);
                    secondaryKpisInserted = true;
                }

                groupRowCount++;
                if (groupRowCount % 3 == 0) {
                    createInlineEditorRow(
                            section.getId(),
                            rowOrder++,
                            buildInlineEditorTitle(group.sousDomaine().getNom(), groupRowCount / 3),
                            buildInlineEditorContent(group.sousDomaine().getNom(), "groupe de 3 rangées"));
                }
            }

            createInlineEditorRow(
                    section.getId(),
                    rowOrder++,
                    "Synthese " + defaultString(group.sousDomaine().getNom(), "sous-domaine"),
                    buildInlineEditorContent(group.sousDomaine().getNom(), "fin de section sous-domaine"),
                    SECTION_EDITOR_ROW_HEIGHT_PX);
        }

        if (shouldInsertSecondaryKpis && !secondaryKpisInserted) {
            createSecondaryKpiRow(section.getId(), rowOrder, perimeterIndicators);
        }
    }

    private void createEditorSection(Long dashboardId, CategorieAnalytique category, Domaine canonicalDomain) {
        String editorSectionTitle = buildMainEditorTitle(category, canonicalDomain);
        TbdSection section = tbdSectionRepository.save(TbdSection.builder()
                .dashboardId(dashboardId)
                .label(editorSectionTitle)
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
                .heightPx(SECTION_EDITOR_ROW_HEIGHT_PX)
                .createdBy(SEED_MARKER)
                .lastModifiedBy(SEED_MARKER)
                .build());

        tbdWidgetRepository.save(TbdWidget.builder()
                .rowId(row.getId())
                .type("EDITOR")
                .titre(editorSectionTitle)
                .ordre(1)
                .sizePercent(100)
                .contentJson(buildEditorTemplate(category, canonicalDomain))
                .actif(true)
                .createdBy(SEED_MARKER)
                .lastModifiedBy(SEED_MARKER)
                .build());
    }

    private void createChartWidget(Long rowId, Indicateur indicator, String title, int order, int sizePercent) {
        tbdWidgetRepository.save(TbdWidget.builder()
                .rowId(rowId)
                .type("CHART")
                .titre(title)
                .indicateur(indicator)
                .ordre(order)
                .sizePercent(sizePercent)
                .actif(true)
                .createdBy(SEED_MARKER)
                .lastModifiedBy(SEED_MARKER)
                .build());
    }

    private void createEmptyWidget(Long rowId, int order, int sizePercent, String title) {
        tbdWidgetRepository.save(TbdWidget.builder()
                .rowId(rowId)
                .type("EMPTY")
                .titre(title)
                .ordre(order)
                .sizePercent(sizePercent)
                .actif(true)
                .createdBy(SEED_MARKER)
                .lastModifiedBy(SEED_MARKER)
                .build());
    }

    private void createInlineEditorRow(Long sectionId, int rowOrder, String title, String contentJson) {
        createInlineEditorRow(sectionId, rowOrder, title, contentJson, INLINE_EDITOR_ROW_HEIGHT_PX);
    }

    private void createInlineEditorRow(Long sectionId, int rowOrder, String title, String contentJson, int heightPx) {
        TbdWidgetRow editorRow = tbdWidgetRowRepository.save(TbdWidgetRow.builder()
                .sectionId(sectionId)
                .ordre(rowOrder)
                .sizePercent(100)
                .heightPx(heightPx)
                .createdBy(SEED_MARKER)
                .lastModifiedBy(SEED_MARKER)
                .build());

        tbdWidgetRepository.save(TbdWidget.builder()
                .rowId(editorRow.getId())
                .type("EDITOR")
                .titre(title)
                .ordre(1)
                .sizePercent(100)
                .contentJson(contentJson)
                .actif(true)
                .createdBy(SEED_MARKER)
                .lastModifiedBy(SEED_MARKER)
                .build());
    }

    private List<KpiSeedCandidate> selectTopKpiCandidates(List<Indicateur> perimeterIndicators) {
        return selectTopKpiCandidates(perimeterIndicators, 0);
    }

    private List<KpiSeedCandidate> selectTopKpiCandidates(List<Indicateur> perimeterIndicators, int skip) {
        if (perimeterIndicators == null || perimeterIndicators.isEmpty()) {
            return Collections.emptyList();
        }
        return perimeterIndicators.stream()
                .skip(Math.max(0, skip))
                .map(this::buildKpiSeedCandidate)
                .filter(Objects::nonNull)
                .limit(4)
                .collect(Collectors.toList());
    }

    private KpiSeedCandidate buildKpiSeedCandidate(Indicateur indicator) {
        if (indicator == null || Boolean.FALSE.equals(indicator.getActif())) {
            return null;
        }
        DonneeIndicateur latestDonnee = extractLatestTemporalDonnee(indicator);
        if (latestDonnee == null) {
            return null;
        }
        String latestDate = extractTemporalValue(indicator, latestDonnee);
        if (!hasText(latestDate)) {
            return null;
        }
        return new KpiSeedCandidate(indicator, latestDonnee, latestDate);
    }

    private ChiffreCle createOrReuseIndicatorKpi(KpiSeedCandidate candidate, int paletteIndex, boolean secondaryStyle) {
        String libelle = secondaryStyle
                ? indicatorDisplayTitle(candidate.indicator()) + " - focus"
                : indicatorDisplayTitle(candidate.indicator());
        Optional<ChiffreCle> existing = chiffreCleService.findByLibelle(libelle);
        if (existing.isPresent()) {
            return existing.get();
        }

        ChiffreCleRequestDto request = new ChiffreCleRequestDto();
        request.setLibelle(libelle);
        request.setDescription(candidate.indicator().getDescription());
        request.setValeur(candidate.donnee().getValeur());
        request.setUnite(candidate.indicator().getUnite());
        request.setActif(Boolean.TRUE);
        request.setAfficherDate(Boolean.TRUE);
        request.setAfficherDescription(Boolean.FALSE);
        request.setModeSource(KpiModeSource.INDICATEUR_VALUE);
        request.setFormatType(resolveKpiFormatType(candidate.indicator()));
        request.setEvolutionMode(KpiEvolutionMode.NONE);
        request.setStyleJson(resolveSeedStyleJson(paletteIndex, secondaryStyle));
        request.setIndicateur(buildReferenceDto(candidate.indicator().getId()));
        request.setDonneeIndicateur(buildReferenceDto(candidate.donnee().getId()));

        try {
            return chiffreCleService.create(request);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Impossible de creer le KPI seed pour l'indicateur '" + libelle + "'.",
                    exception);
        }
    }

    private KpiFormatType resolveKpiFormatType(Indicateur indicator) {
        String unite = indicator != null ? indicator.getUnite() : null;
        return "%".equals(unite) ? KpiFormatType.PERCENT : KpiFormatType.NUMBER;
    }

    private void createSecondaryKpiRow(Long sectionId, int rowOrder, List<Indicateur> perimeterIndicators) {
        List<KpiSeedCandidate> candidates = selectTopKpiCandidates(perimeterIndicators, 4);
        if (candidates.isEmpty()) {
            return;
        }

        TbdWidgetRow row = tbdWidgetRowRepository.save(TbdWidgetRow.builder()
                .sectionId(sectionId)
                .ordre(rowOrder)
                .sizePercent(100)
                .heightPx(KPI_SECONDARY_ROW_HEIGHT_PX)
                .createdBy(SEED_MARKER)
                .lastModifiedBy(SEED_MARKER)
                .build());

        for (int index = 0; index < 4; index++) {
            KpiSeedCandidate candidate = candidates.size() > index ? candidates.get(index) : null;
            if (candidate == null) {
                createEmptyWidget(row.getId(), index + 1, 25, "KPI indisponible");
                continue;
            }

            ChiffreCle seededKpi = createOrReuseIndicatorKpi(candidate, index, true);
            tbdWidgetRepository.save(TbdWidget.builder()
                    .rowId(row.getId())
                    .type("KPI_CARD")
                    .titre(indicatorDisplayTitle(candidate.indicator()))
                    .ordre(index + 1)
                    .sizePercent(25)
                    .kpiId(seededKpi.getId())
                    .actif(true)
                    .createdBy(SEED_MARKER)
                    .lastModifiedBy(SEED_MARKER)
                    .build());
        }
    }

    private DonneeIndicateur extractLatestTemporalDonnee(Indicateur indicator) {
        if (indicator == null || indicator.getDonnees() == null || indicator.getDonnees().isEmpty()) {
            return null;
        }
        Long temporalDimensionId = indicator.getIndicateurDimensions().stream()
                .filter(Objects::nonNull)
                .filter(indicateurDimension -> Boolean.TRUE.equals(indicateurDimension.getTemporelle()))
                .map(IndicateurDimension::getDimension)
                .filter(Objects::nonNull)
                .map(dimension -> dimension.getId())
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        if (temporalDimensionId == null) {
            return null;
        }

        return indicator.getDonnees().stream()
                .filter(Objects::nonNull)
                .filter(donnee -> hasText(extractTemporalValue(temporalDimensionId, donnee)))
                .max((left, right) -> compareTemporalValues(
                        extractTemporalValue(temporalDimensionId, left),
                        extractTemporalValue(temporalDimensionId, right)))
                .orElse(null);
    }

    private String extractTemporalValue(Indicateur indicator, DonneeIndicateur donnee) {
        if (indicator == null || donnee == null) {
            return null;
        }
        Long temporalDimensionId = indicator.getIndicateurDimensions().stream()
                .filter(Objects::nonNull)
                .filter(indicateurDimension -> Boolean.TRUE.equals(indicateurDimension.getTemporelle()))
                .map(IndicateurDimension::getDimension)
                .filter(Objects::nonNull)
                .map(dimension -> dimension.getId())
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        if (temporalDimensionId == null) {
            return null;
        }
        return extractTemporalValue(temporalDimensionId, donnee);
    }

    private String extractTemporalValue(Long temporalDimensionId, DonneeIndicateur donnee) {
        if (temporalDimensionId == null || donnee == null || donnee.getValeurDimensions() == null) {
            return null;
        }
        return donnee.getValeurDimensions().stream()
                .filter(Objects::nonNull)
                .filter(valeurDimension -> valeurDimension.getDimension() != null)
                .filter(valeurDimension -> temporalDimensionId.equals(valeurDimension.getDimension().getId()))
                .map(ValeurDimension::getValeur)
                .filter(TbdCategoryDashboardSeeder::hasText)
                .findFirst()
                .orElse(null);
    }

    private int compareTemporalValues(String left, String right) {
        if (!hasText(left) && !hasText(right)) {
            return 0;
        }
        if (!hasText(left)) {
            return -1;
        }
        if (!hasText(right)) {
            return 1;
        }
        try {
            return Integer.compare(Integer.parseInt(left.trim()), Integer.parseInt(right.trim()));
        } catch (NumberFormatException ignored) {
            return left.trim().compareTo(right.trim());
        }
    }

    private String resolveSeedStyleJson(int index, boolean secondaryStyle) {
        SeedKpiStylePalette[] palettes = secondaryStyle ? KPI_SECONDARY_SEED_STYLES : KPI_SEED_STYLES;
        SeedKpiStylePalette palette = palettes[Math.floorMod(index, palettes.length)];
        return palette.toStyleJson();
    }

    private Dto buildReferenceDto(Long id) {
        Dto dto = new Dto();
        dto.setId(id);
        return dto;
    }

    private Indicateur hydrateIndicatorForSeed(Indicateur indicator) {
        if (indicator == null || !hasText(indicator.getNom())) {
            return null;
        }
        return indicateurService.findByNomWithDonneesAndDimensions(indicator.getNom()).orElse(indicator);
    }

    private boolean hasSeedableIndicatorData(Indicateur indicator) {
        if (indicator == null || indicator.getDonnees() == null || indicator.getDonnees().isEmpty()) {
            return false;
        }

        return indicator.getDonnees().stream()
                .filter(Objects::nonNull)
                .anyMatch(donnee -> hasText(donnee.getValeur())
                        || (donnee.getValeurDimensions() != null && donnee.getValeurDimensions().stream()
                                .filter(Objects::nonNull)
                                .map(ValeurDimension::getValeur)
                                .anyMatch(TbdCategoryDashboardSeeder::hasText)));
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
        String categoryLabel = defaultString(category.getLibelle(), "Sans libelle");
        return "Ce tableau de bord initial a ete genere automatiquement pour la categorie analytique '"
                + categoryLabel
                + "'. Il propose une premiere lecture des principaux KPI et indicateurs disponibles, "
                + "avec une structure prete a etre enrichie par les equipes metier. "
                + "Utilisez cette base pour completer l'analyse, ajouter le contexte utile "
                + "et affiner progressivement le contenu avant publication finale.";
    }

    private String buildEditorTemplate(CategorieAnalytique category, Domaine canonicalDomain) {
        String categoryLabel = escapeHtml(defaultString(category.getLibelle(), "Categorie"));
        String domainLabel = escapeHtml(defaultString(canonicalDomain.getNom(), "Domaine"));
        EditorialTheme theme = resolveEditorialTheme(
                category != null ? category.getLibelle() : null,
                canonicalDomain != null ? canonicalDomain.getNom() : null,
                null);
        String html = """
                <p><strong>Lecture d'ensemble du tableau de bord</strong></p>
                <p>Le present espace editorial accompagne la categorie <em>%s</em> rattachee au domaine <em>%s</em>.</p>
                <p>%s</p>
                <ul>
                  <li>%s</li>
                  <li>%s</li>
                  <li>%s</li>
                  <li>%s</li>
                </ul>
                <p><em>Ce contenu est genere comme base de travail et doit etre consolide par les equipes metier avant publication.</em></p>
                """
                .formatted(
                        categoryLabel,
                        domainLabel,
                        escapeHtml(theme.intro()),
                        escapeHtml(theme.bullet1()),
                        escapeHtml(theme.bullet2()),
                        escapeHtml(theme.bullet3()),
                        escapeHtml(theme.bullet4()));
        return analyticsSeedJsonBuilder.editorContent(html, "#fcfbf8", "#1f2937");
    }

    private String buildInlineEditorTitle(String sousDomaineName, int blockIndex) {
        EditorialTheme theme = resolveEditorialTheme(sousDomaineName, null, null);
        return theme.inlineTitlePrefix() + " " + defaultString(sousDomaineName, "sous-domaine") + " " + blockIndex;
    }

    private String buildMainEditorTitle(CategorieAnalytique category, Domaine canonicalDomain) {
        EditorialTheme theme = resolveEditorialTheme(
                category != null ? category.getLibelle() : null,
                canonicalDomain != null ? canonicalDomain.getNom() : null,
                null);
        return theme.mainTitle();
    }

    private String buildInlineEditorContent(String sousDomaineName, String marker) {
        String sousDomaineLabel = escapeHtml(defaultString(sousDomaineName, "Sous-domaine"));
        EditorialTheme theme = resolveEditorialTheme(sousDomaineName, marker, null);
        String html = """
                <p><strong>Repere de lecture</strong></p>
                <p>Ce bloc editorial accompagne le sous-domaine <em>%s</em> et sert a contextualiser les visualisations voisines.</p>
                <ul>
                  <li>%s</li>
                  <li>%s</li>
                  <li>%s</li>
                </ul>
                <p><em>Repere seed: %s.</em></p>
                """
                .formatted(
                        sousDomaineLabel,
                        escapeHtml(theme.inline1()),
                        escapeHtml(theme.inline2()),
                        escapeHtml(theme.inline3()),
                        escapeHtml(marker));
        return analyticsSeedJsonBuilder.editorContent(html, "#fcfbf8", "#1f2937");
    }

    private EditorialTheme resolveEditorialTheme(String first, String second, String third) {
        String joined = normalizeForTheme(first + " " + second + " " + third);

        if (containsAny(joined, "salaire", "remuneration", "revenu")) {
            return new EditorialTheme(
                    "Analyse des remunerations",
                    "Cette synthese a vocation a mettre en perspective le niveau, la dispersion et l'evolution des remunerations observees dans le perimetre analyse.",
                    "Comparer les niveaux de salaire selon les profils, secteurs ou territoires les plus significatifs.",
                    "Identifier les evolutions recentes, les effets de rattrapage ou les poches de decalage persistantes.",
                    "Preciser les facteurs explicatifs possibles : qualification, structure d'emploi, saisonnalite ou formalisation.",
                    "Conclure sur les principaux enjeux d'attractivite, de pouvoir d'achat ou d'equite de remuneration.",
                    "Lecture salaires",
                    "Resumer les variations de niveau de remuneration les plus visibles sur ce bloc.",
                    "Signaler les ecarts entre profils ou territoires qui meritent une lecture prioritaire.",
                    "Formuler une interpretation courte sur la dynamique salariale observee.");
        }

        if (containsAny(joined, "emploi", "chomage", "insertion", "intermediation", "marche-du-travail", "travail")) {
            return new EditorialTheme(
                    "Analyse du marche du travail",
                    "Cette synthese doit aider a lire les dynamiques d'emploi, d'insertion ou de tension du marche du travail sur la periode recente.",
                    "Presenter les evolutions majeures concernant l'acces a l'emploi, les volumes ou les taux les plus structurants.",
                    "Mettre en evidence les profils, territoires ou segments du marche qui progressent, stagnent ou reculent.",
                    "Ajouter les facteurs d'explication utiles : saisonnalite, conjoncture, qualification, genre ou age.",
                    "Conclure avec les implications possibles pour l'accompagnement, le placement ou le suivi des publics.",
                    "Lecture emploi",
                    "Resumer les dynamiques d'emploi ou d'insertion qui ressortent de ce groupe d'indicateurs.",
                    "Signaler les tensions, decrochages ou reprises visibles selon les publics ou les territoires.",
                    "Ajouter une lecture metier courte sur les implications pour le suivi du marche du travail.");
        }

        if (containsAny(joined, "entreprise", "entrepreneuriat", "creation", "productivite", "competitivite", "investissement")) {
            return new EditorialTheme(
                    "Analyse des dynamiques d'entreprise",
                    "Cette synthese vise a eclairer les dynamiques de creation, de developpement ou de performance des entreprises suivies dans ce perimetre.",
                    "Mettre en avant les indicateurs qui traduisent le mieux le rythme de creation, d'expansion ou de ralentissement.",
                    "Comparer les profils d'entreprises, branches ou zones qui concentrent les evolutions les plus notables.",
                    "Documenter les facteurs de contexte : financement, demande, climat des affaires ou organisation productive.",
                    "Conclure sur les enjeux de competitivite, de resilience ou d'accompagnement des acteurs economiques.",
                    "Lecture entreprises",
                    "Resumer les dynamiques de creation, de performance ou de structuration visibles sur ce bloc.",
                    "Souligner les contrastes entre segments, branches ou implantations les plus marquants.",
                    "Ajouter une interpretation concise sur les leviers ou freins qui ressortent.");
        }

        if (containsAny(joined, "formation", "education", "diplome", "competence", "apprentissage")) {
            return new EditorialTheme(
                    "Analyse formation et competences",
                    "Cette synthese doit faciliter la lecture des parcours de formation, des acquis en competences et de leur articulation avec l'insertion professionnelle.",
                    "Presenter les niveaux, progressions ou disparites les plus visibles selon les filieres et les publics.",
                    "Mettre en avant les transitions vers l'emploi, les points de rupture ou les segments qui performent le mieux.",
                    "Completer avec les facteurs explicatifs utiles : niveau de diplome, specialite, territoire ou type d'etablissement.",
                    "Conclure sur les priorites de rapprochement entre offre de formation et besoins du marche du travail.",
                    "Lecture formation",
                    "Resumer les enseignements les plus utiles sur les parcours, resultats ou debouches visibles ici.",
                    "Signaler les ecarts de performance ou d'insertion a surveiller dans ce sous-domaine.",
                    "Ajouter une courte interpretation sur l'adequation formation-emploi.");
        }

        if (containsAny(joined, "loi", "inspection", "conflit", "securite", "relations-professionnelles", "controle")) {
            return new EditorialTheme(
                    "Analyse controle et conformite",
                    "Cette synthese a pour objectif de restituer les principaux constats sur l'application de la reglementation, les situations de controle et les eventuelles tensions sociales.",
                    "Mettre en avant les volumes, taux ou situations qui traduisent le mieux l'etat du respect des obligations observees.",
                    "Comparer les secteurs, territoires ou types de situation ou les ecarts sont les plus marquants.",
                    "Ajouter les explications utiles : intensite des controles, nature des infractions, cadre sectoriel ou risque social.",
                    "Conclure sur les priorites de prevention, de suivi ou de mediation qui decoulent de la lecture du bloc.",
                    "Lecture conformite",
                    "Resumer les constats de conformite, de controle ou de tension sociale les plus saillants.",
                    "Signaler les situations atypiques ou recurrentes qui meritent un suivi particulier.",
                    "Ajouter une lecture metier courte sur les risques ou actions prioritaires.");
        }

        return new EditorialTheme(
                "Analyse et tendances",
                "Il permet d'introduire les principaux enseignements du tableau de bord avant d'entrer dans le detail des indicateurs et des KPI affiches plus haut.",
                "Presenter les evolutions majeures observees sur la periode la plus recente.",
                "Mettre en avant les ruptures, contrastes territoriaux ou profils qui ressortent le plus.",
                "Ajouter les facteurs d'explication, limites de lecture et points de vigilance utiles.",
                "Conclure avec une courte synthese orientee decision ou suivi operationnel.",
                "Lecture metier",
                "Resumer les faits marquants qui ressortent de ce groupe d'indicateurs.",
                "Signaler les evolutions atypiques, decrochages ou convergences utiles a retenir.",
                "Ajouter une interpretation courte pour aider la lecture metier du bloc.");
    }

    private boolean containsAny(String value, String... tokens) {
        for (String token : tokens) {
            if (value.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private String normalizeForTheme(String value) {
        return normalize(value).replace('-', ' ');
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

    private record KpiSeedCandidate(Indicateur indicator, DonneeIndicateur donnee, String latestDate) {
    }

    private record EditorialTheme(
            String mainTitle,
            String intro,
            String bullet1,
            String bullet2,
            String bullet3,
            String bullet4,
            String inlineTitlePrefix,
            String inline1,
            String inline2,
            String inline3) {
    }

    private record SeedKpiStylePalette(
            String backgroundColor,
            String textColor,
            String borderColor,
            String shadow,
            int borderTopWidth,
            int borderRightWidth,
            int borderBottomWidth,
            int borderLeftWidth) {
        private SeedKpiStylePalette(String backgroundColor, String textColor, String borderColor, String shadow) {
            this(backgroundColor, textColor, borderColor, shadow, 1, 1, 1, 1);
        }

        private String toStyleJson() {
            return """
                    {"backgroundColor":"%s","textColor":"%s","borderTopColor":"%s","borderRightColor":"%s","borderBottomColor":"%s","borderLeftColor":"%s","borderTopWidth":%d,"borderRightWidth":%d,"borderBottomWidth":%d,"borderLeftWidth":%d,"borderTopLeftRadius":0,"borderTopRightRadius":0,"borderBottomRightRadius":0,"borderBottomLeftRadius":0,"paddingTop":4,"paddingRight":4,"paddingBottom":4,"paddingLeft":4,"shadow":"%s","textAlign":"center"}
                    """
                    .formatted(
                            backgroundColor,
                            textColor,
                            borderColor,
                            borderColor,
                            borderColor,
                            borderColor,
                            borderTopWidth,
                            borderRightWidth,
                            borderBottomWidth,
                            borderLeftWidth,
                            shadow);
        }
    }
}
