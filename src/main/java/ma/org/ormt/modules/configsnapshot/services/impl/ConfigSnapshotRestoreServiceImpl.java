package ma.org.ormt.modules.configsnapshot.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.commun.base.dto.Dto;
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
import ma.org.ormt.modules.chiffres.association.domaine.repository.ChiffreCleDomaineRepository;
import ma.org.ormt.modules.chiffres.dtos.request.ChiffreCleRequestDto;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.chiffres.models.enums.KpiEvolutionMode;
import ma.org.ormt.modules.chiffres.models.enums.KpiFormatType;
import ma.org.ormt.modules.chiffres.models.enums.KpiModeSource;
import ma.org.ormt.modules.chiffres.repositories.ChiffreCleRepository;
import ma.org.ormt.modules.chiffres.services.ChiffreCleService;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotAnalyticsPortalFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotChiffresFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotDonneesFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotGraphesFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotIndicatorsFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotManifestDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotRestoreRequestDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotRestoreResultDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotTbdDashboardsFileDto;
import ma.org.ormt.modules.configsnapshot.services.ConfigSnapshotRestoreService;
import ma.org.ormt.modules.dashboard.tbgroup.models.TbGroup;
import ma.org.ormt.modules.dashboard.tbgroup.repositories.TbGroupRepository;
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
import ma.org.ormt.modules.espaces.models.Espace;
import ma.org.ormt.modules.espaces.repositories.EspaceRepository;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.dimension.repositories.DimensionRepository;
import ma.org.ormt.modules.indicateurs.donnee.dtos.request.DonneeIndicateurRequestDto;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.donnee.repositories.DonneeIndicateurRepository;
import ma.org.ormt.modules.indicateurs.donnee.services.DonneeIndicateurService;
import ma.org.ormt.modules.indicateurs.graphe.configuration.models.GrapheConfiguration;
import ma.org.ormt.modules.indicateurs.graphe.configuration.repositories.GrapheConfigurationRepository;
import ma.org.ormt.modules.indicateurs.graphe.type.models.GrapheType;
import ma.org.ormt.modules.indicateurs.graphe.type.repositories.GrapheTypeRepository;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.repository.IndicateurDimensionRepository;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.repositories.IndicateurRepository;
import ma.org.ormt.modules.indicateurs.source.models.Source;
import ma.org.ormt.modules.indicateurs.source.repositories.SourceRepository;
import ma.org.ormt.modules.indicateurs.valeurdimension.dtos.request.ValeurDimensionRequestDto;

@Service
@RequiredArgsConstructor
@Log4j2
public class ConfigSnapshotRestoreServiceImpl implements ConfigSnapshotRestoreService {

    private static final int SUPPORTED_SNAPSHOT_VERSION = 1;

    private final EntityManager entityManager;
    private final SourceRepository sourceRepository;
    private final DimensionRepository dimensionRepository;
    private final IndicateurRepository indicateurRepository;
    private final IndicateurDimensionRepository indicateurDimensionRepository;
    private final DonneeIndicateurRepository donneeIndicateurRepository;
    private final DonneeIndicateurService donneeIndicateurService;
    private final GrapheConfigurationRepository grapheConfigurationRepository;
    private final GrapheTypeRepository grapheTypeRepository;
    private final ChiffreCleRepository chiffreCleRepository;
    private final ChiffreCleDomaineRepository chiffreCleDomaineRepository;
    private final ChiffreCleService chiffreCleService;
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
    private final EspaceRepository espaceRepository;
    private final TbGroupRepository tbGroupRepository;
    private final DomaineRepository domaineRepository;
    private final SousDomaineRepository sousDomaineRepository;
    private final ConfigSnapshotJsonSupport jsonSupport;
    private final ConfigSnapshotZipService zipService;

    @Override
    @Transactional
    public ConfigSnapshotRestoreResultDto restoreSnapshot(ConfigSnapshotRestoreRequestDto requestDto) {
        if (requestDto == null || requestDto.getFile() == null || requestDto.getFile().isEmpty()) {
            throw new IllegalArgumentException("Snapshot file is required.");
        }

        ConfigSnapshotArchive archive = readArchive(requestDto);
        validateManifest(archive.getManifest());

        if (requestDto.isReplace()) {
            purgeSnapshotScope();
        }

        RestoreContext context = new RestoreContext();

        Map<String, Source> sourcesByKey = restoreSources(archive, context);
        Map<String, Dimension> dimensionsByNom = restoreDimensions(archive.getIndicators(), context);
        Map<String, Indicateur> indicateursByNom = restoreIndicateurs(archive.getIndicators(), sourcesByKey, dimensionsByNom,
                context);
        Map<String, DonneeIndicateur> donneesByKey = restoreDonnees(archive.getDonnees(), indicateursByNom, dimensionsByNom,
                context);
        restoreGraphes(archive.getGraphes(), indicateursByNom, context);
        Map<String, ChiffreCle> chiffresByLibelle = restoreChiffres(archive.getChiffres(), indicateursByNom, donneesByKey,
                context);
        Map<String, TbdDashboard> dashboardsByNom = restoreDashboards(archive.getDashboards(), indicateursByNom, chiffresByLibelle,
                sourcesByKey, context);
        restoreAnalytics(archive.getAnalytics(), dashboardsByNom, context);

        String restoredBy = currentUsername();
        log.info("Config snapshot v{} restored by {} with replace={}", archive.getManifest().getSnapshotVersion(), restoredBy,
                requestDto.isReplace());

        return ConfigSnapshotRestoreResultDto.builder()
                .snapshotVersion(archive.getManifest().getSnapshotVersion())
                .replacedExistingData(requestDto.isReplace())
                .modulesRestored(archive.getManifest().getModulesIncluded())
                .sourcesCreated(context.sourcesCreated)
                .dimensionsCreated(context.dimensionsCreated)
                .indicateursCreated(context.indicateursCreated)
                .donneesCreated(context.donneesCreated)
                .graphesCreated(context.graphesCreated)
                .chiffresClesCreated(context.chiffresCreated)
                .dashboardsCreated(context.dashboardsCreated)
                .analyticsDomainesCreated(context.analyticsDomainesCreated)
                .analyticsCategoriesCreated(context.analyticsCategoriesCreated)
                .restoredBy(restoredBy)
                .build();
    }

    private ConfigSnapshotArchive readArchive(ConfigSnapshotRestoreRequestDto requestDto) {
        try {
            Map<String, byte[]> entries = zipService.readEntries(requestDto.getFile().getBytes());
            return ConfigSnapshotArchive.builder()
                    .manifest(readRequired(entries, "manifest.json", ConfigSnapshotManifestDto.class))
                    .indicators(readRequired(entries, "indicateurs.json", ConfigSnapshotIndicatorsFileDto.class))
                    .donnees(readRequired(entries, "donnees_indicateurs.json", ConfigSnapshotDonneesFileDto.class))
                    .graphes(readRequired(entries, "graphe_configurations.json", ConfigSnapshotGraphesFileDto.class))
                    .chiffres(readRequired(entries, "chiffres_cles.json", ConfigSnapshotChiffresFileDto.class))
                    .dashboards(readRequired(entries, "tbd_dashboards.json", ConfigSnapshotTbdDashboardsFileDto.class))
                    .analytics(readRequired(entries, "analytics_portal.json", ConfigSnapshotAnalyticsPortalFileDto.class))
                    .build();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Invalid snapshot zip.", exception);
        }
    }

    private <T> T readRequired(Map<String, byte[]> entries, String name, Class<T> targetClass) throws IOException {
        byte[] payload = entries.get(name);
        if (payload == null) {
            throw new IllegalArgumentException("Missing required snapshot entry: " + name);
        }
        return jsonSupport.fromJsonBytes(payload, targetClass);
    }

    private void validateManifest(ConfigSnapshotManifestDto manifest) {
        if (manifest == null || manifest.getSnapshotVersion() == null) {
            throw new IllegalArgumentException("Snapshot manifest is missing its version.");
        }
        if (manifest.getSnapshotVersion() != SUPPORTED_SNAPSHOT_VERSION) {
            throw new IllegalArgumentException(
                    "Unsupported snapshot version " + manifest.getSnapshotVersion() + ". Expected "
                            + SUPPORTED_SNAPSHOT_VERSION + ".");
        }
    }

    private void purgeSnapshotScope() {
        List<String> truncateStatements = List.of(
                "TRUNCATE TABLE categorie_analytique_section RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE domaine_analytique_section RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE tb_group_domaine_analytique RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE espace_domaine_analytique RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE categorie_analytique RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE domaine_analytique RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE tbd_widget RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE tbd_widget_row RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE tbd_section RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE tbd_source_listing RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE tbd_dashboard RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE chiffre_cle_domaine RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE chiffre_cle RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE valeur_dimension RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE donnee_indicateur RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE graphe_configuration RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE indicateur_dimension RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE indicateur_sous_domaine RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE indicateur RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE dimension RESTART IDENTITY CASCADE",
                "TRUNCATE TABLE source RESTART IDENTITY CASCADE");

        truncateStatements.forEach(statement -> entityManager.createNativeQuery(statement).executeUpdate());
    }

    private Map<String, Source> restoreSources(ConfigSnapshotArchive archive, RestoreContext context) {
        Map<String, ConfigSnapshotIndicatorsFileDto.SourceRefDto> uniqueSources = new LinkedHashMap<>();

        archive.getIndicators().getIndicateurs().forEach(indicator -> {
            if (indicator.getSource() == null) {
                return;
            }
            String key = normalizeSourceKey(indicator.getSource().getAbreviation(), indicator.getSource().getNom());
            if (key != null) {
                uniqueSources.putIfAbsent(key, indicator.getSource());
            }
        });

        archive.getDashboards().getTbdDashboards().forEach(dashboard -> dashboard.getSources().forEach(source -> {
            String key = normalizeSourceKey(source.getSourceAbreviation(), source.getSourceNom());
            if (key == null || uniqueSources.containsKey(key)) {
                return;
            }
            uniqueSources.put(key, ConfigSnapshotIndicatorsFileDto.SourceRefDto.builder()
                    .nom(source.getSourceNom())
                    .abreviation(source.getSourceAbreviation())
                    .build());
        }));

        Map<String, Source> created = new LinkedHashMap<>();
        uniqueSources.forEach((key, value) -> {
            Source source = sourceRepository.save(Source.builder()
                    .nom(jsonSupport.trimToNull(value.getNom()))
                    .abreviation(jsonSupport.trimToNull(value.getAbreviation()))
                    .description(jsonSupport.trimToNull(value.getDescription()))
                    .url(jsonSupport.trimToNull(value.getUrl()))
                    .build());
            created.put(key, source);
            context.sourcesCreated++;
        });
        return created;
    }

    private Map<String, Dimension> restoreDimensions(ConfigSnapshotIndicatorsFileDto indicatorsFile, RestoreContext context) {
        Map<String, ConfigSnapshotIndicatorsFileDto.DimensionBindingDto> uniqueDimensions = new LinkedHashMap<>();
        indicatorsFile.getIndicateurs().forEach(indicator -> indicator.getDimensions().forEach(dimension -> {
            if (jsonSupport.hasText(dimension.getNom())) {
                uniqueDimensions.putIfAbsent(dimension.getNom().toLowerCase(), dimension);
            }
        }));

        Map<String, Dimension> created = new LinkedHashMap<>();
        uniqueDimensions.values().stream()
                .sorted(Comparator.comparing(ConfigSnapshotIndicatorsFileDto.DimensionBindingDto::getNom,
                        Comparator.nullsLast(String::compareToIgnoreCase)))
                .forEach(dimension -> {
                    Dimension saved = dimensionRepository.save(Dimension.builder()
                            .nom(dimension.getNom())
                            .libelle(dimension.getLibelle())
                            .type(dimension.getType())
                            .description(dimension.getDescription())
                            .build());
                    created.put(saved.getNom().toLowerCase(), saved);
                    context.dimensionsCreated++;
                });
        return created;
    }

    private Map<String, Indicateur> restoreIndicateurs(ConfigSnapshotIndicatorsFileDto indicatorsFile,
            Map<String, Source> sourcesByKey,
            Map<String, Dimension> dimensionsByNom,
            RestoreContext context) {
        Map<String, Indicateur> created = new LinkedHashMap<>();

        for (ConfigSnapshotIndicatorsFileDto.IndicatorDto indicatorDto : indicatorsFile.getIndicateurs()) {
            Indicateur indicator = Indicateur.builder()
                    .nom(indicatorDto.getNom())
                    .titre(indicatorDto.getTitre())
                    .description(indicatorDto.getDescription())
                    .abreviation(indicatorDto.getAbreviation())
                    .categorie(indicatorDto.getCategorie())
                    .actif(indicatorDto.getActif())
                    .typeTb(indicatorDto.getTypeTb())
                    .unite(indicatorDto.getUnite())
                    .regleCalcul(indicatorDto.getRegleCalcul())
                    .source(resolveSource(indicatorDto.getSource(), sourcesByKey))
                    .sousDomaines(resolveSousDomaines(indicatorDto.getSousDomaines()))
                    .build();
            Indicateur saved = indicateurRepository.save(indicator);
            created.put(saved.getNom(), saved);
            context.indicateursCreated++;

            for (ConfigSnapshotIndicatorsFileDto.DimensionBindingDto dimensionDto : indicatorDto.getDimensions()) {
                Dimension dimension = dimensionsByNom.get(dimensionDto.getNom().toLowerCase());
                if (dimension == null) {
                    throw new EntityNotFoundException("Missing dimension during indicator restore: " + dimensionDto.getNom());
                }
                indicateurDimensionRepository.save(IndicateurDimension.builder()
                        .indicateur(saved)
                        .dimension(dimension)
                        .principale(Boolean.TRUE.equals(dimensionDto.getPrincipale()))
                        .temporelle(Boolean.TRUE.equals(dimensionDto.getTemporelle()))
                        .build());
            }
        }

        return created;
    }

    private Source resolveSource(ConfigSnapshotIndicatorsFileDto.SourceRefDto sourceRef, Map<String, Source> sourcesByKey) {
        if (sourceRef == null) {
            return null;
        }
        String key = normalizeSourceKey(sourceRef.getAbreviation(), sourceRef.getNom());
        return key == null ? null : sourcesByKey.get(key);
    }

    private List<SousDomaine> resolveSousDomaines(List<ConfigSnapshotIndicatorsFileDto.SousDomaineRefDto> refs) {
        List<SousDomaine> sousDomaines = new ArrayList<>();
        for (ConfigSnapshotIndicatorsFileDto.SousDomaineRefDto ref : refs) {
            Domaine domaine = domaineRepository.findByNom(ref.getDomaineNom())
                    .orElseThrow(() -> new EntityNotFoundException("Domaine not found for snapshot restore: " + ref.getDomaineNom()));
            SousDomaine sousDomaine = sousDomaineRepository.findByNom(ref.getSousDomaineNom())
                    .filter(value -> value.getDomaine() != null && Objects.equals(value.getDomaine().getId(), domaine.getId()))
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Sous-domaine not found for snapshot restore: " + ref.getSousDomaineNom()));
            sousDomaines.add(sousDomaine);
        }
        return sousDomaines;
    }

    private Map<String, DonneeIndicateur> restoreDonnees(ConfigSnapshotDonneesFileDto donneesFile,
            Map<String, Indicateur> indicateursByNom,
            Map<String, Dimension> dimensionsByNom,
            RestoreContext context) {
        Map<String, DonneeIndicateur> created = new LinkedHashMap<>();

        for (ConfigSnapshotDonneesFileDto.RowDto rowDto : donneesFile.getDonneesIndicateurs()) {
            Indicateur indicator = indicateursByNom.get(rowDto.getIndicateurNom());
            if (indicator == null) {
                throw new EntityNotFoundException("Indicateur not found for data restore: " + rowDto.getIndicateurNom());
            }

            DonneeIndicateurRequestDto requestDto = new DonneeIndicateurRequestDto();
            requestDto.setValeur(rowDto.getValeur());
            List<ValeurDimensionRequestDto> dimensionValues = rowDto.getDimensions().stream()
                    .map(dimension -> {
                        Dimension persistedDimension = dimensionsByNom.get(dimension.getDimensionNom().toLowerCase());
                        if (persistedDimension == null) {
                            throw new EntityNotFoundException(
                                    "Dimension not found for data restore: " + dimension.getDimensionNom());
                        }
                        ValeurDimensionRequestDto valueDto = new ValeurDimensionRequestDto();
                        valueDto.setDimension(persistedDimension);
                        valueDto.setValeur(dimension.getValeur());
                        return valueDto;
                    })
                    .toList();
            requestDto.setValeurDimensions(dimensionValues);

            DonneeIndicateur saved = donneeIndicateurService.create(indicator.getId(), requestDto);
            created.put(buildDonneeKey(indicator.getNom(), rowDto.getDimensions()), saved);
            context.donneesCreated++;
        }

        return created;
    }

    private Map<String, ChiffreCle> restoreChiffres(ConfigSnapshotChiffresFileDto chiffresFile,
            Map<String, Indicateur> indicateursByNom,
            Map<String, DonneeIndicateur> donneesByKey,
            RestoreContext context) {
        Map<String, ChiffreCle> created = new LinkedHashMap<>();

        for (ConfigSnapshotChiffresFileDto.ChiffreDto chiffreDto : chiffresFile.getChiffresCles()) {
            ChiffreCleRequestDto requestDto = new ChiffreCleRequestDto();
            requestDto.setLibelle(chiffreDto.getLibelle());
            requestDto.setValeur(chiffreDto.getValeur());
            requestDto.setUnite(chiffreDto.getUnite());
            requestDto.setDescription(chiffreDto.getDescription());
            requestDto.setAfficherDate(chiffreDto.getAfficherDate());
            requestDto.setAfficherDescription(chiffreDto.getAfficherDescription());
            requestDto.setActif(Boolean.TRUE.equals(chiffreDto.getActif()));
            requestDto.setAccessType(chiffreDto.getAccessType());
            requestDto.setModeSource(chiffreDto.getModeSource() == null ? KpiModeSource.MANUAL
                    : KpiModeSource.valueOf(chiffreDto.getModeSource()));
            requestDto.setFormatType(chiffreDto.getFormatType() == null ? KpiFormatType.NUMBER
                    : KpiFormatType.valueOf(chiffreDto.getFormatType()));
            requestDto.setPrefixLabel(chiffreDto.getPrefixLabel());
            requestDto.setSuffixLabel(chiffreDto.getSuffixLabel());
            requestDto.setEvolutionMode(chiffreDto.getEvolutionMode() == null ? KpiEvolutionMode.NONE
                    : KpiEvolutionMode.valueOf(chiffreDto.getEvolutionMode()));
            requestDto.setMetadataJson(chiffreDto.getMetadataJson());
            requestDto.setStyleJson(chiffreDto.getStyleJson());

            if (jsonSupport.hasText(chiffreDto.getIndicateurNom())) {
                Indicateur indicator = indicateursByNom.get(chiffreDto.getIndicateurNom());
                if (indicator == null) {
                    throw new EntityNotFoundException("Indicateur not found for KPI restore: " + chiffreDto.getIndicateurNom());
                }
                requestDto.setIndicateur(referenceDto(indicator.getId()));
            }

            if (chiffreDto.getDonneeReference() != null) {
                String donneeKey = buildDonneeKey(chiffreDto.getDonneeReference().getIndicateurNom(),
                        chiffreDto.getDonneeReference().getDimensions());
                DonneeIndicateur donnee = donneesByKey.get(donneeKey);
                if (donnee == null) {
                    throw new EntityNotFoundException("Donnee reference not found for KPI restore: " + chiffreDto.getLibelle());
                }
                requestDto.setDonneeIndicateur(referenceDto(donnee.getId()));
            }

            try {
                ChiffreCle saved = chiffreCleService.create(requestDto);
                created.put(saved.getLibelle(), saved);
                context.chiffresCreated++;

                for (String domaineNom : chiffreDto.getDomaineNoms()) {
                    Domaine domaine = domaineRepository.findByNom(domaineNom)
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Domaine not found for KPI restore: " + domaineNom));
                    chiffreCleDomaineRepository.save(ChiffreCleDomaine.builder()
                            .chiffreCle(saved)
                            .domaine(domaine)
                            .build());
                }
            } catch (Exception exception) {
                throw new IllegalArgumentException("Unable to restore KPI " + chiffreDto.getLibelle(), exception);
            }
        }
        return created;
    }

    private void restoreGraphes(ConfigSnapshotGraphesFileDto graphesFile,
            Map<String, Indicateur> indicateursByNom,
            RestoreContext context) {
        for (ConfigSnapshotGraphesFileDto.GrapheDto grapheDto : graphesFile.getGrapheConfigurations()) {
            Indicateur indicator = indicateursByNom.get(grapheDto.getIndicateurNom());
            if (indicator == null) {
                throw new EntityNotFoundException(
                        "Indicateur not found for graph restore: " + grapheDto.getIndicateurNom());
            }

            GrapheType grapheType = resolveGrapheType(grapheDto);
            grapheConfigurationRepository.save(GrapheConfiguration.builder()
                    .indicateur(indicator)
                    .grapheType(grapheType)
                    .nom(grapheDto.getNom())
                    .dimensionMappingJson(jsonSupport.coalesce(grapheDto.getDimensionMappingJson(), "{}"))
                    .chartOptionsJson(grapheDto.getChartOptionsJson())
                    .chartSpecVersion(grapheDto.getChartSpecVersion())
                    .chartSpecJson(grapheDto.getChartSpecJson())
                    .configSystem(jsonSupport.coalesce(grapheDto.getConfigSystem(), "legacy"))
                    .isDefault(Boolean.TRUE.equals(grapheDto.getIsDefault()))
                    .build());
            context.graphesCreated++;
        }
    }

    private GrapheType resolveGrapheType(ConfigSnapshotGraphesFileDto.GrapheDto grapheDto) {
        if (jsonSupport.hasText(grapheDto.getGrapheTypeCode())) {
            Optional<GrapheType> byCode = grapheTypeRepository.findByCode(grapheDto.getGrapheTypeCode());
            if (byCode.isPresent()) {
                return byCode.get();
            }
        }
        if (jsonSupport.hasText(grapheDto.getGrapheTypeNom())) {
            return grapheTypeRepository.findByNom(grapheDto.getGrapheTypeNom())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Graphe type not found for snapshot restore: " + grapheDto.getGrapheTypeNom()));
        }
        throw new EntityNotFoundException("Missing graph type in snapshot restore.");
    }

    private Map<String, TbdDashboard> restoreDashboards(ConfigSnapshotTbdDashboardsFileDto dashboardsFile,
            Map<String, Indicateur> indicateursByNom,
            Map<String, ChiffreCle> chiffresByLibelle,
            Map<String, Source> sourcesByKey,
            RestoreContext context) {
        Map<String, TbdDashboard> created = new LinkedHashMap<>();

        for (ConfigSnapshotTbdDashboardsFileDto.DashboardDto dashboardDto : dashboardsFile.getTbdDashboards()) {
            TbdDashboard dashboard = tbdDashboardRepository.save(TbdDashboard.builder()
                    .nom(dashboardDto.getNom())
                    .titre(dashboardDto.getTitre())
                    .sousTitre(dashboardDto.getSousTitre())
                    .description(dashboardDto.getDescription())
                    .sourceText(dashboardDto.getSourceText())
                    .actif(dashboardDto.getActif() == null ? true : dashboardDto.getActif())
                    .status(jsonSupport.coalesce(dashboardDto.getStatus(), "DRAFT"))
                    .build());
            created.put(dashboard.getNom(), dashboard);
            context.dashboardsCreated++;

            for (ConfigSnapshotTbdDashboardsFileDto.SourceRefDto sourceDto : dashboardDto.getSources()) {
                Source source = sourcesByKey.get(normalizeSourceKey(sourceDto.getSourceAbreviation(), sourceDto.getSourceNom()));
                if (source == null) {
                    throw new EntityNotFoundException("Source not found for dashboard restore: " + sourceDto.getSourceNom());
                }
                tbdSourceListingRepository.save(TbdSourceListing.builder()
                        .dashboardId(dashboard.getId())
                        .source(source)
                        .ordre(sourceDto.getOrdre() == null ? 0 : sourceDto.getOrdre())
                        .build());
            }

            for (ConfigSnapshotTbdDashboardsFileDto.SectionDto sectionDto : dashboardDto.getSections()) {
                TbdSection section = tbdSectionRepository.save(TbdSection.builder()
                        .dashboardId(dashboard.getId())
                        .label(sectionDto.getLabel())
                        .ordre(sectionDto.getOrdre() == null ? 0 : sectionDto.getOrdre())
                        .sizePercent(sectionDto.getSizePercent() == null ? 33 : sectionDto.getSizePercent())
                        .actif(sectionDto.getActif() == null ? true : sectionDto.getActif())
                        .build());

                for (ConfigSnapshotTbdDashboardsFileDto.RowDto rowDto : sectionDto.getRows()) {
                    TbdWidgetRow row = tbdWidgetRowRepository.save(TbdWidgetRow.builder()
                            .sectionId(section.getId())
                            .ordre(rowDto.getOrdre() == null ? 0 : rowDto.getOrdre())
                            .sizePercent(rowDto.getSizePercent() == null ? 50 : rowDto.getSizePercent())
                            .heightPx(rowDto.getHeightPx() == null ? 200 : rowDto.getHeightPx())
                            .build());

                    for (ConfigSnapshotTbdDashboardsFileDto.WidgetDto widgetDto : rowDto.getWidgets()) {
                        TbdWidget widget = TbdWidget.builder()
                                .rowId(row.getId())
                                .type(widgetDto.getType())
                                .contentJson(widgetDto.getContentJson())
                                .titre(widgetDto.getTitre())
                                .ordre(widgetDto.getOrdre() == null ? 0 : widgetDto.getOrdre())
                                .sizePercent(widgetDto.getSizePercent() == null ? 50 : widgetDto.getSizePercent())
                                .actif(widgetDto.getActif() == null ? true : widgetDto.getActif())
                                .build();

                        if (jsonSupport.hasText(widgetDto.getIndicateurNom())) {
                            Indicateur indicator = indicateursByNom.get(widgetDto.getIndicateurNom());
                            if (indicator == null) {
                                throw new EntityNotFoundException(
                                        "Indicateur not found for widget restore: " + widgetDto.getIndicateurNom());
                            }
                            widget.setIndicateur(indicator);
                        }
                        if (jsonSupport.hasText(widgetDto.getChiffreCleLibelle())) {
                            ChiffreCle chiffre = chiffresByLibelle.get(widgetDto.getChiffreCleLibelle());
                            if (chiffre == null) {
                                throw new EntityNotFoundException("KPI not found for widget restore: "
                                        + widgetDto.getChiffreCleLibelle());
                            }
                            widget.setKpiId(chiffre.getId());
                        }

                        tbdWidgetRepository.save(widget);
                    }
                }
            }
        }

        return created;
    }

    private void restoreAnalytics(ConfigSnapshotAnalyticsPortalFileDto analyticsFile,
            Map<String, TbdDashboard> dashboardsByNom,
            RestoreContext context) {
        Map<String, DomaineAnalytique> domainsBySlug = new LinkedHashMap<>();

        for (ConfigSnapshotAnalyticsPortalFileDto.DomaineAnalytiqueDto domainDto : analyticsFile.getDomainesAnalytiques()) {
            DomaineAnalytique domain = domaineAnalytiqueRepository.save(DomaineAnalytique.builder()
                    .nom(domainDto.getNom())
                    .titre(domainDto.getTitre())
                    .description(domainDto.getDescription())
                    .apropos(domainDto.getApropos())
                    .imageUrl(domainDto.getImageUrl())
                    .slug(domainDto.getSlug())
                    .sourceThemeKey(domainDto.getSourceThemeKey())
                    .metadataJson(domainDto.getMetadataJson())
                    .actif(domainDto.getActif() == null ? true : domainDto.getActif())
                    .build());
            domainsBySlug.put(domain.getSlug(), domain);
            context.analyticsDomainesCreated++;

            for (ConfigSnapshotAnalyticsPortalFileDto.SectionDto sectionDto : domainDto.getSections()) {
                domaineAnalytiqueSectionRepository.save(DomaineAnalytiqueSection.builder()
                        .domaineAnalytique(domain)
                        .type(sectionDto.getType())
                        .titre(sectionDto.getTitre())
                        .contentJson(sectionDto.getContentJson())
                        .ordre(sectionDto.getOrdre() == null ? 0 : sectionDto.getOrdre())
                        .actif(sectionDto.getActif() == null ? true : sectionDto.getActif())
                        .build());
            }
        }

        for (ConfigSnapshotAnalyticsPortalFileDto.CategorieAnalytiqueDto categoryDto : analyticsFile.getCategoriesAnalytiques()) {
            DomaineAnalytique domain = domainsBySlug.get(categoryDto.getDomaineSlug());
            if (domain == null) {
                throw new EntityNotFoundException("Domaine analytique not found for category restore: " + categoryDto.getDomaineSlug());
            }
            TbdDashboard dashboard = categoryDto.getTbdNom() == null ? null : dashboardsByNom.get(categoryDto.getTbdNom());
            if (categoryDto.getTbdNom() != null && dashboard == null) {
                throw new EntityNotFoundException("Dashboard not found for analytics category restore: " + categoryDto.getTbdNom());
            }

            CategorieAnalytique category = categorieAnalytiqueRepository.save(CategorieAnalytique.builder()
                    .domaineAnalytique(domain)
                    .tbdDashboard(dashboard)
                    .nom(categoryDto.getNom())
                    .libelle(categoryDto.getLibelle())
                    .description(categoryDto.getDescription())
                    .slug(categoryDto.getSlug())
                    .ordre(categoryDto.getOrdre() == null ? 0 : categoryDto.getOrdre())
                    .actif(categoryDto.getActif() == null ? true : categoryDto.getActif())
                    .build());
            context.analyticsCategoriesCreated++;

            for (ConfigSnapshotAnalyticsPortalFileDto.SectionDto sectionDto : categoryDto.getSections()) {
                categorieAnalytiqueSectionRepository.save(CategorieAnalytiqueSection.builder()
                        .categorieAnalytique(category)
                        .type(sectionDto.getType())
                        .titre(sectionDto.getTitre())
                        .contentJson(sectionDto.getContentJson())
                        .ordre(sectionDto.getOrdre() == null ? 0 : sectionDto.getOrdre())
                        .actif(sectionDto.getActif() == null ? true : sectionDto.getActif())
                        .build());
            }
        }

        for (ConfigSnapshotAnalyticsPortalFileDto.EspaceLinkDto espaceDto : analyticsFile.getEspaces()) {
            Espace espace = espaceRepository.findByNom(espaceDto.getEspaceNom())
                    .orElseThrow(() -> new EntityNotFoundException("Espace not found for analytics restore: " + espaceDto.getEspaceNom()));
            for (int index = 0; index < espaceDto.getDomaines().size(); index++) {
                DomaineAnalytique domain = domainsBySlug.get(espaceDto.getDomaines().get(index));
                if (domain == null) {
                    throw new EntityNotFoundException(
                            "Domaine analytique not found for espace link restore: " + espaceDto.getDomaines().get(index));
                }
                espaceDomaineAnalytiqueRepository.save(EspaceDomaineAnalytique.builder()
                        .espace(espace)
                        .domaineAnalytique(domain)
                        .ordre(index)
                        .build());
            }
        }

        for (ConfigSnapshotAnalyticsPortalFileDto.TbGroupLinkDto tbGroupDto : analyticsFile.getTbGroups()) {
            TbGroup tbGroup = tbGroupRepository.findByNom(tbGroupDto.getTbGroupNom())
                    .orElseThrow(
                            () -> new EntityNotFoundException("TB group not found for analytics restore: " + tbGroupDto.getTbGroupNom()));
            for (int index = 0; index < tbGroupDto.getDomaines().size(); index++) {
                DomaineAnalytique domain = domainsBySlug.get(tbGroupDto.getDomaines().get(index));
                if (domain == null) {
                    throw new EntityNotFoundException(
                            "Domaine analytique not found for tb_group link restore: " + tbGroupDto.getDomaines().get(index));
                }
                tbGroupDomaineAnalytiqueRepository.save(TbGroupDomaineAnalytique.builder()
                        .tbGroup(tbGroup)
                        .domaineAnalytique(domain)
                        .ordre(index)
                        .build());
            }
        }
    }

    private Dto referenceDto(Long id) {
        Dto dto = new Dto();
        dto.setId(id);
        return dto;
    }

    private String buildDonneeKey(String indicateurNom, List<ConfigSnapshotDonneesFileDto.DimensionValueDto> dimensions) {
        String dimensionKey = dimensions == null ? ""
                : dimensions.stream()
                        .sorted(Comparator.comparing(ConfigSnapshotDonneesFileDto.DimensionValueDto::getDimensionNom,
                                Comparator.nullsLast(String::compareToIgnoreCase)))
                        .map(value -> value.getDimensionNom() + "=" + value.getValeur())
                        .collect(Collectors.joining("|"));
        return indicateurNom + "::" + dimensionKey;
    }

    private String normalizeSourceKey(String abreviation, String nom) {
        String key = jsonSupport.coalesce(jsonSupport.trimToNull(abreviation), jsonSupport.trimToNull(nom));
        return key == null ? null : key.toLowerCase();
    }

    private String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return "system";
        }
        return authentication.getName();
    }

    private static class RestoreContext {
        private int sourcesCreated;
        private int dimensionsCreated;
        private int indicateursCreated;
        private int donneesCreated;
        private int graphesCreated;
        private int chiffresCreated;
        private int dashboardsCreated;
        private int analyticsDomainesCreated;
        private int analyticsCategoriesCreated;
    }
}
