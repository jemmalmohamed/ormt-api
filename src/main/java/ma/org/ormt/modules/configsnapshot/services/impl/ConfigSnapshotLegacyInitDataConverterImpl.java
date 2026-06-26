package ma.org.ormt.modules.configsnapshot.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotAnalyticsPortalFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotChiffresFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotDonneesFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotGraphesFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotIndicatorsFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotTbdDashboardsFileDto;
import ma.org.ormt.modules.configsnapshot.services.ConfigSnapshotLegacyInitDataConverter;
import ma.org.ormt.modules.configsnapshot.services.ConfigSnapshotZipEntry;

@Service
public class ConfigSnapshotLegacyInitDataConverterImpl implements ConfigSnapshotLegacyInitDataConverter {

    private static final String LEGACY_ROOT = "legacy-init-data/init-data";
    private static final String LEGACY_TERRITORY = "national";

    private final ConfigSnapshotJsonSupport jsonSupport;

    public ConfigSnapshotLegacyInitDataConverterImpl(ConfigSnapshotJsonSupport jsonSupport) {
        this.jsonSupport = jsonSupport;
    }

    @Override
    public List<ConfigSnapshotZipEntry> buildLegacyEntries(ConfigSnapshotArchive archive) throws IOException {
        List<ConfigSnapshotZipEntry> entries = new ArrayList<>();

        entries.add(jsonEntry(LEGACY_ROOT + "/sources/sources.json",
                collectLegacySources(archive.getIndicators(), archive.getDashboards())));
        entries.add(jsonEntry(LEGACY_ROOT + "/chiffres/chiffre_cle.json", archive.getChiffres().getChiffresCles()));
        entries.add(jsonEntry(LEGACY_ROOT + "/tb_group/" + LEGACY_TERRITORY + "/tbd_dashboards.json",
                archive.getDashboards().getTbdDashboards()));
        entries.add(jsonEntry(LEGACY_ROOT + "/analytics/portail_analytique.json", archive.getAnalytics()));
        entries.add(jsonEntry(LEGACY_ROOT + "/tb_group/" + LEGACY_TERRITORY + "/tb_groups.json",
                archive.getAnalytics().getTbGroups().stream()
                        .map(link -> {
                            Map<String, Object> json = new LinkedHashMap<>();
                            json.put("nom", link.getTbGroupNom());
                            json.put("description", null);
                            json.put("actif", true);
                            return json;
                        })
                        .distinct()
                        .toList()));

        Map<String, DomainSeed> domains = buildDomainSeeds(archive.getIndicators(), archive.getDonnees(), archive.getGraphes());
        for (DomainSeed domain : domains.values()) {
            String domainFolder = LEGACY_ROOT + "/domaines/" + LEGACY_TERRITORY + "/" + jsonSupport.safeFileSegment(domain.nom);

            Map<String, Object> domainFile = new LinkedHashMap<>();
            domainFile.put("nom", domain.nom);
            domainFile.put("description", domain.description);
            domainFile.put("actif", domain.actif);
            entries.add(jsonEntry(domainFolder + "/" + jsonSupport.safeFileSegment(domain.nom) + ".domaine.json", domainFile));

            for (SubDomainSeed subDomain : domain.subDomains.values()) {
                String subFolder = domainFolder + "/sous-domaines/" + jsonSupport.safeFileSegment(subDomain.nom);
                Map<String, Object> subDomainFile = new LinkedHashMap<>();
                subDomainFile.put("nom", subDomain.nom);
                subDomainFile.put("description", subDomain.description);
                subDomainFile.put("actif", subDomain.actif);
                subDomainFile.put("indicateurs", subDomain.indicators);
                entries.add(jsonEntry(subFolder + "/" + jsonSupport.safeFileSegment(subDomain.nom) + ".json", subDomainFile));

                for (Map.Entry<String, List<Map<String, Object>>> dataEntry : subDomain.indicatorData.entrySet()) {
                    Map<String, Object> indicatorData = new LinkedHashMap<>();
                    indicatorData.put("indicateur", dataEntry.getKey());
                    indicatorData.put("data", dataEntry.getValue());
                    entries.add(jsonEntry(subFolder + "/data/" + jsonSupport.safeFileSegment(dataEntry.getKey()) + ".json",
                            indicatorData));
                }
            }
        }

        return entries;
    }

    private List<Map<String, Object>> collectLegacySources(ConfigSnapshotIndicatorsFileDto indicators,
            ConfigSnapshotTbdDashboardsFileDto dashboards) {
        Map<String, Map<String, Object>> sources = new LinkedHashMap<>();

        indicators.getIndicateurs().forEach(indicator -> {
            if (indicator.getSource() == null) {
                return;
            }
            String key = jsonSupport.coalesce(indicator.getSource().getAbreviation(), indicator.getSource().getNom());
            if (key == null) {
                return;
            }
            Map<String, Object> sourceJson = new LinkedHashMap<>();
            sourceJson.put("nom", indicator.getSource().getNom());
            sourceJson.put("description", indicator.getSource().getDescription());
            sourceJson.put("url", indicator.getSource().getUrl());
            sourceJson.put("abreviation", indicator.getSource().getAbreviation());
            sources.putIfAbsent(key.toLowerCase(), sourceJson);
        });

        dashboards.getTbdDashboards().forEach(dashboard -> dashboard.getSources().forEach(source -> {
            String key = jsonSupport.coalesce(source.getSourceAbreviation(), source.getSourceNom());
            if (key == null) {
                return;
            }
            Map<String, Object> sourceJson = new LinkedHashMap<>();
            sourceJson.put("nom", source.getSourceNom());
            sourceJson.put("description", null);
            sourceJson.put("url", null);
            sourceJson.put("abreviation", source.getSourceAbreviation());
            sources.putIfAbsent(key.toLowerCase(), sourceJson);
        }));

        return new ArrayList<>(sources.values());
    }

    private Map<String, DomainSeed> buildDomainSeeds(ConfigSnapshotIndicatorsFileDto indicators,
            ConfigSnapshotDonneesFileDto donnees,
            ConfigSnapshotGraphesFileDto graphes) {
        Map<String, List<ConfigSnapshotDonneesFileDto.RowDto>> donneesByIndicateur = donnees.getDonneesIndicateurs().stream()
                .collect(Collectors.groupingBy(ConfigSnapshotDonneesFileDto.RowDto::getIndicateurNom, LinkedHashMap::new,
                        Collectors.toList()));
        Map<String, List<ConfigSnapshotGraphesFileDto.GrapheDto>> graphesByIndicateur = graphes.getGrapheConfigurations().stream()
                .collect(Collectors.groupingBy(ConfigSnapshotGraphesFileDto.GrapheDto::getIndicateurNom, LinkedHashMap::new,
                        Collectors.toList()));

        Map<String, DomainSeed> domains = new LinkedHashMap<>();
        for (ConfigSnapshotIndicatorsFileDto.IndicatorDto indicator : indicators.getIndicateurs()) {
            for (ConfigSnapshotIndicatorsFileDto.SousDomaineRefDto sousDomaine : indicator.getSousDomaines()) {
                if (!jsonSupport.hasText(sousDomaine.getDomaineNom()) || !jsonSupport.hasText(sousDomaine.getSousDomaineNom())) {
                    continue;
                }

                DomainSeed domainSeed = domains.computeIfAbsent(sousDomaine.getDomaineNom(), key -> new DomainSeed(
                        sousDomaine.getDomaineNom(),
                        sousDomaine.getDomaineDescription(),
                        Boolean.TRUE.equals(sousDomaine.getDomaineActif()),
                        new LinkedHashMap<>()));
                SubDomainSeed subDomainSeed = domainSeed.subDomains.computeIfAbsent(sousDomaine.getSousDomaineNom(),
                        key -> new SubDomainSeed(
                                sousDomaine.getSousDomaineNom(),
                                sousDomaine.getSousDomaineDescription(),
                                Boolean.TRUE.equals(sousDomaine.getSousDomaineActif()),
                                new ArrayList<>(),
                                new LinkedHashMap<>()));

                Map<String, Object> indicatorJson = new LinkedHashMap<>();
                indicatorJson.put("nom", indicator.getNom());
                indicatorJson.put("titre", indicator.getTitre());
                indicatorJson.put("categorie", indicator.getCategorie());
                indicatorJson.put("actif", indicator.getActif());
                indicatorJson.put("abreviation", indicator.getAbreviation());
                indicatorJson.put("description", indicator.getDescription());
                indicatorJson.put("typeTb", indicator.getTypeTb());
                indicatorJson.put("source", indicator.getSource() != null ? indicator.getSource().getAbreviation() : null);
                indicatorJson.put("regleCalcul", indicator.getRegleCalcul());
                indicatorJson.put("unite", indicator.getUnite());
                indicatorJson.put("dimensions", indicator.getDimensions().stream()
                        .map(dimension -> {
                            Map<String, Object> dimensionJson = new LinkedHashMap<>();
                            dimensionJson.put("nom", dimension.getNom());
                            dimensionJson.put("libelle", dimension.getLibelle());
                            dimensionJson.put("type", dimension.getType());
                            Map<String, Object> association = new LinkedHashMap<>();
                            association.put("principale", dimension.getPrincipale());
                            association.put("temporelle", dimension.getTemporelle());
                            dimensionJson.put("association", association);
                            return dimensionJson;
                        })
                        .toList());
                indicatorJson.put("grapheConfigurations",
                        graphesByIndicateur.getOrDefault(indicator.getNom(), List.of()).stream()
                                .map(graphe -> {
                                    Map<String, Object> grapheJson = new LinkedHashMap<>();
                                    grapheJson.put("nom", graphe.getNom());
                                    grapheJson.put("grapheTypeCode",
                                            jsonSupport.coalesce(graphe.getGrapheTypeCode(), graphe.getGrapheTypeNom()));
                                    grapheJson.put("isDefault", graphe.getIsDefault());
                                    grapheJson.put("dimensionMappingJson", graphe.getDimensionMappingJson());
                                    grapheJson.put("chartOptionsJson", graphe.getChartOptionsJson());
                                    grapheJson.put("chartSpecVersion", graphe.getChartSpecVersion());
                                    grapheJson.put("chartSpecJson", graphe.getChartSpecJson());
                                    grapheJson.put("configSystem", graphe.getConfigSystem());
                                    return grapheJson;
                                })
                                .toList());
                subDomainSeed.indicators.add(indicatorJson);

                List<Map<String, Object>> dataRows = donneesByIndicateur.getOrDefault(indicator.getNom(), List.of()).stream()
                        .map(row -> {
                            Map<String, Object> rowJson = new LinkedHashMap<>();
                            row.getDimensions().stream()
                                    .sorted(Comparator.comparing(ConfigSnapshotDonneesFileDto.DimensionValueDto::getDimensionNom,
                                            Comparator.nullsLast(String::compareToIgnoreCase)))
                                    .forEach(dimension -> rowJson.put(dimension.getDimensionNom(), dimension.getValeur()));
                            rowJson.put("valeur", row.getValeur());
                            return rowJson;
                        })
                        .toList();
                if (!dataRows.isEmpty()) {
                    subDomainSeed.indicatorData.put(indicator.getNom(), dataRows);
                }
            }
        }
        return domains;
    }

    private ConfigSnapshotZipEntry jsonEntry(String path, Object payload) throws IOException {
        return new ConfigSnapshotZipEntry(path, jsonSupport.toJsonBytes(payload));
    }

    private record DomainSeed(
            String nom,
            String description,
            boolean actif,
            Map<String, SubDomainSeed> subDomains) {
    }

    private record SubDomainSeed(
            String nom,
            String description,
            boolean actif,
            List<Map<String, Object>> indicators,
            Map<String, List<Map<String, Object>>> indicatorData) {
    }
}
