package ma.org.ormt.modules.configsnapshot.services.impl;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.analytics.association.espace.EspaceDomaineAnalytique;
import ma.org.ormt.modules.analytics.association.espace.EspaceDomaineAnalytiqueRepository;
import ma.org.ormt.modules.analytics.association.tbgroup.TbGroupDomaineAnalytique;
import ma.org.ormt.modules.analytics.association.tbgroup.TbGroupDomaineAnalytiqueRepository;
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytique;
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytiqueSection;
import ma.org.ormt.modules.analytics.category.repositories.CategorieAnalytiqueRepository;
import ma.org.ormt.modules.analytics.category.repositories.CategorieAnalytiqueSectionRepository;
import ma.org.ormt.modules.analytics.domain.models.DomaineAnalytique;
import ma.org.ormt.modules.analytics.domain.models.DomaineAnalytiqueSection;
import ma.org.ormt.modules.analytics.domain.repositories.DomaineAnalytiqueRepository;
import ma.org.ormt.modules.analytics.domain.repositories.DomaineAnalytiqueSectionRepository;
import ma.org.ormt.modules.chiffres.association.domaine.ChiffreCleDomaine;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.chiffres.repositories.ChiffreCleRepository;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotAnalyticsPortalFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotChiffresFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotDonneesFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotExportRequestDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotGraphesFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotIndicatorsFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotManifestDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotTbdDashboardsFileDto;
import ma.org.ormt.modules.configsnapshot.services.ConfigSnapshotExportService;
import ma.org.ormt.modules.configsnapshot.services.ConfigSnapshotLegacyInitDataConverter;
import ma.org.ormt.modules.configsnapshot.services.ConfigSnapshotZipEntry;
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
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.donnee.repositories.DonneeIndicateurRepository;
import ma.org.ormt.modules.indicateurs.graphe.configuration.models.GrapheConfiguration;
import ma.org.ormt.modules.indicateurs.graphe.configuration.repositories.GrapheConfigurationRepository;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.repositories.IndicateurRepository;
import ma.org.ormt.modules.indicateurs.valeurdimension.models.ValeurDimension;

@Service
@RequiredArgsConstructor
public class ConfigSnapshotExportServiceImpl implements ConfigSnapshotExportService {

    private static final List<String> MODULES = List.of(
            "indicateurs",
            "donnees_indicateurs",
            "graphe_configurations",
            "chiffres_cles",
            "tbd_dashboards",
            "analytics_portal");

    private final IndicateurRepository indicateurRepository;
    private final DonneeIndicateurRepository donneeIndicateurRepository;
    private final GrapheConfigurationRepository grapheConfigurationRepository;
    private final ChiffreCleRepository chiffreCleRepository;
    private final TbdDashboardRepository tbdDashboardRepository;
    private final TbdSourceListingRepository tbdSourceListingRepository;
    private final TbdSectionRepository tbdSectionRepository;
    private final TbdWidgetRowRepository tbdWidgetRowRepository;
    private final TbdWidgetRepository tbdWidgetRepository;
    private final DomaineAnalytiqueRepository domaineAnalytiqueRepository;
    private final DomaineAnalytiqueSectionRepository domaineAnalytiqueSectionRepository;
    private final CategorieAnalytiqueRepository categorieAnalytiqueRepository;
    private final CategorieAnalytiqueSectionRepository categorieAnalytiqueSectionRepository;
    private final EspaceDomaineAnalytiqueRepository espaceDomaineAnalytiqueRepository;
    private final TbGroupDomaineAnalytiqueRepository tbGroupDomaineAnalytiqueRepository;
    private final ConfigSnapshotJsonSupport jsonSupport;
    private final ConfigSnapshotZipService zipService;
    private final ConfigSnapshotLegacyInitDataConverter legacyInitDataConverter;

    @Override
    @Transactional(readOnly = true)
    public byte[] exportSnapshot(ConfigSnapshotExportRequestDto requestDto) {
        ConfigSnapshotExportRequestDto effectiveRequest = requestDto == null
                ? ConfigSnapshotExportRequestDto.builder().build()
                : requestDto;

        ConfigSnapshotArchive archive = buildArchive();

        List<ConfigSnapshotZipEntry> entries = new ArrayList<>();
        try {
            entries.add(new ConfigSnapshotZipEntry("manifest.json", jsonSupport.toJsonBytes(archive.getManifest())));
            entries.add(new ConfigSnapshotZipEntry("indicateurs.json", jsonSupport.toJsonBytes(archive.getIndicators())));
            entries.add(new ConfigSnapshotZipEntry("donnees_indicateurs.json", jsonSupport.toJsonBytes(archive.getDonnees())));
            entries.add(new ConfigSnapshotZipEntry("graphe_configurations.json", jsonSupport.toJsonBytes(archive.getGraphes())));
            entries.add(new ConfigSnapshotZipEntry("chiffres_cles.json", jsonSupport.toJsonBytes(archive.getChiffres())));
            entries.add(new ConfigSnapshotZipEntry("tbd_dashboards.json", jsonSupport.toJsonBytes(archive.getDashboards())));
            entries.add(new ConfigSnapshotZipEntry("analytics_portal.json", jsonSupport.toJsonBytes(archive.getAnalytics())));
            if (effectiveRequest.isIncludeLegacyInitData()) {
                entries.addAll(legacyInitDataConverter.buildLegacyEntries(archive));
            }
            return zipService.writeEntries(entries);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to export config snapshot", exception);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportLegacyInitData(ConfigSnapshotExportRequestDto requestDto) {
        ConfigSnapshotArchive archive = buildArchive();
        try {
            return zipService.writeEntries(legacyInitDataConverter.buildLegacyEntries(archive));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to export legacy init-data", exception);
        }
    }

    private ConfigSnapshotArchive buildArchive() {
        return ConfigSnapshotArchive.builder()
                .manifest(buildManifest())
                .indicators(buildIndicatorsFile())
                .donnees(buildDonneesFile())
                .graphes(buildGraphesFile())
                .chiffres(buildChiffresFile())
                .dashboards(buildDashboardsFile())
                .analytics(buildAnalyticsFile())
                .build();
    }

    private ConfigSnapshotManifestDto buildManifest() {
        Map<String, Object> compatibility = new LinkedHashMap<>();
        compatibility.put("restoreMode", "replace");
        compatibility.put("stableKeys", List.of(
                "indicateur.nom",
                "dimension.nom",
                "source.abreviation|nom",
                "dashboard.nom",
                "domaineAnalytique.slug|sourceThemeKey",
                "categorieAnalytique.domaineSlug+slug|nom"));

        Package packageInfo = getClass().getPackage();
        String appVersion = packageInfo != null && packageInfo.getImplementationVersion() != null
                ? packageInfo.getImplementationVersion()
                : "unknown";

        return ConfigSnapshotManifestDto.builder()
                .snapshotVersion(1)
                .exportedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .appVersion(appVersion)
                .modulesIncluded(MODULES)
                .compatibility(compatibility)
                .build();
    }

    private ConfigSnapshotIndicatorsFileDto buildIndicatorsFile() {
        List<Indicateur> indicators = indicateurRepository.findAll().stream()
                .sorted(Comparator.comparing(Indicateur::getNom, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();

        List<ConfigSnapshotIndicatorsFileDto.IndicatorDto> payload = indicators.stream()
                .map(indicator -> ConfigSnapshotIndicatorsFileDto.IndicatorDto.builder()
                        .nom(indicator.getNom())
                        .titre(indicator.getTitre())
                        .description(indicator.getDescription())
                        .abreviation(indicator.getAbreviation())
                        .categorie(indicator.getCategorie())
                        .actif(indicator.getActif())
                        .typeTb(indicator.getTypeTb())
                        .unite(indicator.getUnite())
                        .regleCalcul(indicator.getRegleCalcul())
                        .source(indicator.getSource() == null ? null : ConfigSnapshotIndicatorsFileDto.SourceRefDto.builder()
                                .nom(indicator.getSource().getNom())
                                .abreviation(indicator.getSource().getAbreviation())
                                .description(indicator.getSource().getDescription())
                                .url(indicator.getSource().getUrl())
                                .build())
                        .sousDomaines(indicator.getSousDomaines().stream()
                                .sorted(Comparator.comparing(SousDomaine::getNom, Comparator.nullsLast(String::compareToIgnoreCase)))
                                .map(sousDomaine -> ConfigSnapshotIndicatorsFileDto.SousDomaineRefDto.builder()
                                        .domaineNom(sousDomaine.getDomaine() != null ? sousDomaine.getDomaine().getNom() : null)
                                        .domaineDescription(
                                                sousDomaine.getDomaine() != null ? sousDomaine.getDomaine().getDescription() : null)
                                        .domaineActif(sousDomaine.getDomaine() != null ? sousDomaine.getDomaine().getActif() : null)
                                        .sousDomaineNom(sousDomaine.getNom())
                                        .sousDomaineDescription(sousDomaine.getDescription())
                                        .sousDomaineActif(sousDomaine.getActif())
                                        .sousDomaineOrdre(sousDomaine.getOrdre())
                                        .build())
                                .toList())
                        .dimensions(indicator.getIndicateurDimensions() == null ? List.of()
                                : indicator.getIndicateurDimensions().stream()
                                        .sorted(Comparator.comparing(binding -> binding.getDimension().getNom(),
                                                Comparator.nullsLast(String::compareToIgnoreCase)))
                                        .map(binding -> toDimensionBinding(binding))
                                        .toList())
                        .build())
                .toList();

        return ConfigSnapshotIndicatorsFileDto.builder().indicateurs(payload).build();
    }

    private ConfigSnapshotIndicatorsFileDto.DimensionBindingDto toDimensionBinding(IndicateurDimension binding) {
        return ConfigSnapshotIndicatorsFileDto.DimensionBindingDto.builder()
                .nom(binding.getDimension().getNom())
                .libelle(binding.getDimension().getLibelle())
                .type(binding.getDimension().getType())
                .description(binding.getDimension().getDescription())
                .principale(binding.getPrincipale())
                .temporelle(binding.getTemporelle())
                .build();
    }

    private ConfigSnapshotDonneesFileDto buildDonneesFile() {
        List<ConfigSnapshotDonneesFileDto.RowDto> rows = new ArrayList<>();
        List<Indicateur> indicators = indicateurRepository.findAll().stream()
                .sorted(Comparator.comparing(Indicateur::getNom, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();

        for (Indicateur indicator : indicators) {
            List<DonneeIndicateur> dataRows = donneeIndicateurRepository.findAllByIndicateurId(indicator.getId());
            dataRows.stream()
                    .sorted(Comparator.comparing(this::buildDonneeSortKey))
                    .forEach(data -> rows.add(ConfigSnapshotDonneesFileDto.RowDto.builder()
                            .indicateurNom(indicator.getNom())
                            .valeur(data.getValeur())
                            .dimensions(data.getValeurDimensions() == null ? List.of()
                                    : data.getValeurDimensions().stream()
                                            .sorted(Comparator.comparing(value -> value.getDimension().getNom(),
                                                    Comparator.nullsLast(String::compareToIgnoreCase)))
                                            .map(value -> ConfigSnapshotDonneesFileDto.DimensionValueDto.builder()
                                                    .dimensionNom(value.getDimension().getNom())
                                                    .valeur(value.getValeur())
                                                    .build())
                                            .toList())
                            .build()));
        }

        return ConfigSnapshotDonneesFileDto.builder().donneesIndicateurs(rows).build();
    }

    private String buildDonneeSortKey(DonneeIndicateur donnee) {
        return (donnee.getValeurDimensions() == null ? "" : donnee.getValeurDimensions().stream()
                .sorted(Comparator.comparing(value -> value.getDimension().getNom(), Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(value -> value.getDimension().getNom() + "=" + value.getValeur())
                .collect(Collectors.joining("|")))
                + "::" + Objects.toString(donnee.getValeur(), "");
    }

    private ConfigSnapshotGraphesFileDto buildGraphesFile() {
        List<GrapheConfiguration> configs = grapheConfigurationRepository.findAll().stream()
                .sorted(Comparator.comparing(GrapheConfiguration::getNom, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();

        return ConfigSnapshotGraphesFileDto.builder()
                .grapheConfigurations(configs.stream()
                        .map(config -> ConfigSnapshotGraphesFileDto.GrapheDto.builder()
                                .indicateurNom(config.getIndicateur() != null ? config.getIndicateur().getNom() : null)
                                .nom(config.getNom())
                                .grapheTypeCode(config.getGrapheType() != null ? config.getGrapheType().getCode() : null)
                                .grapheTypeNom(config.getGrapheType() != null ? config.getGrapheType().getNom() : null)
                                .dimensionMappingJson(jsonSupport.normalizeJson(config.getDimensionMappingJson()))
                                .chartOptionsJson(jsonSupport.normalizeJson(config.getChartOptionsJson()))
                                .chartSpecVersion(config.getChartSpecVersion())
                                .chartSpecJson(jsonSupport.normalizeJson(config.getChartSpecJson()))
                                .configSystem(config.getConfigSystem())
                                .isDefault(config.getIsDefault())
                                .build())
                        .toList())
                .build();
    }

    private ConfigSnapshotChiffresFileDto buildChiffresFile() {
        List<ChiffreCle> chiffres = chiffreCleRepository.findAll().stream()
                .sorted(Comparator.comparing(ChiffreCle::getLibelle, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();

        return ConfigSnapshotChiffresFileDto.builder()
                .chiffresCles(chiffres.stream()
                        .map(chiffre -> ConfigSnapshotChiffresFileDto.ChiffreDto.builder()
                                .libelle(chiffre.getLibelle())
                                .valeur(chiffre.getValeur())
                                .unite(chiffre.getUnite())
                                .description(chiffre.getDescription())
                                .afficherDate(chiffre.getAfficherDate())
                                .afficherDescription(chiffre.getAfficherDescription())
                                .actif(chiffre.isActif())
                                .accessType(chiffre.getAccessType())
                                .modeSource(chiffre.getModeSource() == null ? null : chiffre.getModeSource().name())
                                .formatType(chiffre.getFormatType() == null ? null : chiffre.getFormatType().name())
                                .prefixLabel(chiffre.getPrefixLabel())
                                .suffixLabel(chiffre.getSuffixLabel())
                                .evolutionMode(chiffre.getEvolutionMode() == null ? null : chiffre.getEvolutionMode().name())
                                .metadataJson(jsonSupport.normalizeJson(chiffre.getMetadataJson()))
                                .styleJson(jsonSupport.normalizeJson(chiffre.getStyleJson()))
                                .indicateurNom(chiffre.getIndicateur() == null ? null : chiffre.getIndicateur().getNom())
                                .donneeReference(toDonneeReference(chiffre.getDonneeIndicateur()))
                                .domaineNoms(chiffre.getChiffrecleDomaines() == null ? List.of()
                                        : chiffre.getChiffrecleDomaines().stream()
                                                .map(ChiffreCleDomaine::getDomaine)
                                                .filter(Objects::nonNull)
                                                .map(domain -> domain.getNom())
                                                .sorted(String::compareToIgnoreCase)
                                                .toList())
                                .build())
                        .toList())
                .build();
    }

    private ConfigSnapshotChiffresFileDto.DonneeReferenceDto toDonneeReference(DonneeIndicateur donneeIndicateur) {
        if (donneeIndicateur == null || donneeIndicateur.getIndicateur() == null) {
            return null;
        }

        return ConfigSnapshotChiffresFileDto.DonneeReferenceDto.builder()
                .indicateurNom(donneeIndicateur.getIndicateur().getNom())
                .valeur(donneeIndicateur.getValeur())
                .dimensions(donneeIndicateur.getValeurDimensions() == null ? List.of()
                        : donneeIndicateur.getValeurDimensions().stream()
                                .sorted(Comparator.comparing(value -> value.getDimension().getNom(),
                                        Comparator.nullsLast(String::compareToIgnoreCase)))
                                .map(value -> ConfigSnapshotDonneesFileDto.DimensionValueDto.builder()
                                        .dimensionNom(value.getDimension().getNom())
                                        .valeur(value.getValeur())
                                        .build())
                                .toList())
                .build();
    }

    private ConfigSnapshotTbdDashboardsFileDto buildDashboardsFile() {
        List<TbdDashboard> dashboards = tbdDashboardRepository.findAll().stream()
                .sorted(Comparator.comparing(TbdDashboard::getNom, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();

        List<ConfigSnapshotTbdDashboardsFileDto.DashboardDto> payload = dashboards.stream()
                .map(this::toDashboardDto)
                .toList();
        return ConfigSnapshotTbdDashboardsFileDto.builder().tbdDashboards(payload).build();
    }

    private ConfigSnapshotTbdDashboardsFileDto.DashboardDto toDashboardDto(TbdDashboard dashboard) {
        List<TbdSourceListing> sources = tbdSourceListingRepository.findByDashboardIdOrderByOrdreAsc(dashboard.getId());
        List<TbdSection> sections = tbdSectionRepository.findByDashboardIdOrderByOrdreAsc(dashboard.getId());
        List<Long> sectionIds = sections.stream().map(TbdSection::getId).toList();
        List<TbdWidgetRow> rows = sectionIds.isEmpty()
                ? List.of()
                : tbdWidgetRowRepository.findBySectionIdInOrderBySectionIdAscOrdreAsc(sectionIds);
        Map<Long, List<TbdWidgetRow>> rowsBySection = rows.stream()
                .collect(Collectors.groupingBy(TbdWidgetRow::getSectionId, LinkedHashMap::new, Collectors.toList()));
        List<Long> rowIds = rows.stream().map(TbdWidgetRow::getId).toList();
        Map<Long, List<TbdWidget>> widgetsByRow = (rowIds.isEmpty()
                ? List.<TbdWidget>of()
                : tbdWidgetRepository.findByRowIdInOrderByRowIdAscOrdreAsc(rowIds)).stream()
                .collect(Collectors.groupingBy(TbdWidget::getRowId, LinkedHashMap::new, Collectors.toList()));

        return ConfigSnapshotTbdDashboardsFileDto.DashboardDto.builder()
                .nom(dashboard.getNom())
                .titre(dashboard.getTitre())
                .sousTitre(dashboard.getSousTitre())
                .description(dashboard.getDescription())
                .sourceText(dashboard.getSourceText())
                .actif(dashboard.getActif())
                .status(dashboard.getStatus())
                .sources(sources.stream()
                        .map(source -> ConfigSnapshotTbdDashboardsFileDto.SourceRefDto.builder()
                                .sourceNom(source.getSource() != null ? source.getSource().getNom() : null)
                                .sourceAbreviation(source.getSource() != null ? source.getSource().getAbreviation() : null)
                                .ordre(source.getOrdre())
                                .build())
                        .toList())
                .sections(sections.stream()
                        .map(section -> ConfigSnapshotTbdDashboardsFileDto.SectionDto.builder()
                                .label(section.getLabel())
                                .ordre(section.getOrdre())
                                .sizePercent(section.getSizePercent())
                                .actif(section.getActif())
                                .rows(rowsBySection.getOrDefault(section.getId(), List.of()).stream()
                                        .map(row -> ConfigSnapshotTbdDashboardsFileDto.RowDto.builder()
                                                .ordre(row.getOrdre())
                                                .sizePercent(row.getSizePercent())
                                                .heightPx(row.getHeightPx())
                                                .widgets(widgetsByRow.getOrDefault(row.getId(), List.of()).stream()
                                                        .map(widget -> ConfigSnapshotTbdDashboardsFileDto.WidgetDto.builder()
                                                                .type(widget.getType())
                                                                .indicateurNom(widget.getIndicateur() == null ? null
                                                                        : widget.getIndicateur().getNom())
                                                                .chiffreCleLibelle(resolveKpiLabel(widget.getKpiId()))
                                                                .contentJson(jsonSupport.normalizeJson(widget.getContentJson()))
                                                                .titre(widget.getTitre())
                                                                .ordre(widget.getOrdre())
                                                                .sizePercent(widget.getSizePercent())
                                                                .actif(widget.getActif())
                                                                .build())
                                                        .toList())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .build();
    }

    private String resolveKpiLabel(Long kpiId) {
        if (kpiId == null) {
            return null;
        }
        Optional<ChiffreCle> chiffre = chiffreCleRepository.findById(kpiId);
        return chiffre.map(ChiffreCle::getLibelle).orElse(null);
    }

    private ConfigSnapshotAnalyticsPortalFileDto buildAnalyticsFile() {
        List<DomaineAnalytique> domains = domaineAnalytiqueRepository.findAll().stream()
                .sorted(Comparator.comparing(DomaineAnalytique::getSlug, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();
        List<CategorieAnalytique> categories = categorieAnalytiqueRepository.findAll().stream()
                .sorted(Comparator.comparing((CategorieAnalytique category) -> category.getDomaineAnalytique() == null
                        ? ""
                        : category.getDomaineAnalytique().getSlug(), String::compareToIgnoreCase)
                        .thenComparing(CategorieAnalytique::getOrdre, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(CategorieAnalytique::getSlug, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();

        return ConfigSnapshotAnalyticsPortalFileDto.builder()
                .domainesAnalytiques(domains.stream().map(this::toAnalyticsDomainDto).toList())
                .categoriesAnalytiques(categories.stream().map(this::toAnalyticsCategoryDto).toList())
                .espaces(buildEspaceLinks())
                .tbGroups(buildTbGroupLinks())
                .build();
    }

    private ConfigSnapshotAnalyticsPortalFileDto.DomaineAnalytiqueDto toAnalyticsDomainDto(DomaineAnalytique domain) {
        List<DomaineAnalytiqueSection> sections = domaineAnalytiqueSectionRepository
                .findByDomaineAnalytiqueIdOrderByOrdreAsc(domain.getId());
        return ConfigSnapshotAnalyticsPortalFileDto.DomaineAnalytiqueDto.builder()
                .nom(domain.getNom())
                .titre(domain.getTitre())
                .description(domain.getDescription())
                .apropos(domain.getApropos())
                .imageUrl(domain.getImageUrl())
                .slug(domain.getSlug())
                .sourceThemeKey(domain.getSourceThemeKey())
                .metadataJson(jsonSupport.normalizeJson(domain.getMetadataJson()))
                .actif(domain.getActif())
                .sections(sections.stream()
                        .map(section -> ConfigSnapshotAnalyticsPortalFileDto.SectionDto.builder()
                                .type(section.getType())
                                .titre(section.getTitre())
                                .contentJson(jsonSupport.normalizeJson(section.getContentJson()))
                                .ordre(section.getOrdre())
                                .actif(section.getActif())
                                .build())
                        .toList())
                .build();
    }

    private ConfigSnapshotAnalyticsPortalFileDto.CategorieAnalytiqueDto toAnalyticsCategoryDto(CategorieAnalytique category) {
        List<CategorieAnalytiqueSection> sections = categorieAnalytiqueSectionRepository
                .findByCategorieAnalytiqueIdOrderByOrdreAsc(category.getId());
        return ConfigSnapshotAnalyticsPortalFileDto.CategorieAnalytiqueDto.builder()
                .domaineSlug(category.getDomaineAnalytique() == null ? null : category.getDomaineAnalytique().getSlug())
                .nom(category.getNom())
                .libelle(category.getLibelle())
                .description(category.getDescription())
                .slug(category.getSlug())
                .ordre(category.getOrdre())
                .actif(category.getActif())
                .tbdNom(category.getTbdDashboard() == null ? null : category.getTbdDashboard().getNom())
                .sections(sections.stream()
                        .map(section -> ConfigSnapshotAnalyticsPortalFileDto.SectionDto.builder()
                                .type(section.getType())
                                .titre(section.getTitre())
                                .contentJson(jsonSupport.normalizeJson(section.getContentJson()))
                                .ordre(section.getOrdre())
                                .actif(section.getActif())
                                .build())
                        .toList())
                .build();
    }

    private List<ConfigSnapshotAnalyticsPortalFileDto.EspaceLinkDto> buildEspaceLinks() {
        Map<String, List<EspaceDomaineAnalytique>> grouped = espaceDomaineAnalytiqueRepository.findAll().stream()
                .filter(link -> link.getEspace() != null)
                .collect(Collectors.groupingBy(link -> link.getEspace().getNom(), LinkedHashMap::new, Collectors.toList()));

        return grouped.entrySet().stream()
                .map(entry -> ConfigSnapshotAnalyticsPortalFileDto.EspaceLinkDto.builder()
                        .espaceNom(entry.getKey())
                        .domaines(entry.getValue().stream()
                                .sorted(Comparator.comparing(EspaceDomaineAnalytique::getOrdre, Comparator.nullsLast(Integer::compareTo)))
                                .map(EspaceDomaineAnalytique::getDomaineAnalytique)
                                .filter(Objects::nonNull)
                                .map(DomaineAnalytique::getSlug)
                                .toList())
                        .build())
                .toList();
    }

    private List<ConfigSnapshotAnalyticsPortalFileDto.TbGroupLinkDto> buildTbGroupLinks() {
        Map<String, List<TbGroupDomaineAnalytique>> grouped = tbGroupDomaineAnalytiqueRepository.findAll().stream()
                .filter(link -> link.getTbGroup() != null)
                .collect(Collectors.groupingBy(link -> link.getTbGroup().getNom(), LinkedHashMap::new, Collectors.toList()));

        return grouped.entrySet().stream()
                .map(entry -> ConfigSnapshotAnalyticsPortalFileDto.TbGroupLinkDto.builder()
                        .tbGroupNom(entry.getKey())
                        .domaines(entry.getValue().stream()
                                .sorted(Comparator.comparing(TbGroupDomaineAnalytique::getOrdre, Comparator.nullsLast(Integer::compareTo)))
                                .map(TbGroupDomaineAnalytique::getDomaineAnalytique)
                                .filter(Objects::nonNull)
                                .map(DomaineAnalytique::getSlug)
                                .toList())
                        .build())
                .toList();
    }
}
