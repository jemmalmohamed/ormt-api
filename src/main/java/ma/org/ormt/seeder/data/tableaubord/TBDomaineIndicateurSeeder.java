package ma.org.ormt.seeder.data.tableaubord;

import java.io.File;
import java.text.Normalizer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.utilities.files.FileDataService;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.TBDomaineIndicateur;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos.request.TBDomaineIndicateurRequestDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.repository.TBDomaineIndicateurRepository;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.service.TBDomaineIndicateurService;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.TBDomaineDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.services.TBDomaineService;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.IndicateurDto;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@Log4j2
@Component
@Order(6)
@RequiredArgsConstructor
public class TBDomaineIndicateurSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.data_path}")
    private String dataExternalPath;

    @Value("${data.external.territoire}")
    private String territoire;

    private final FileDataService fileDataService;
    private final TBDomaineService tbDomaineService;
    private final IndicateurService indicateurService;
    private final TBDomaineIndicateurService tbDomaineIndicateurService;
    private final TBDomaineIndicateurRepository tbDomaineIndicateurRepository;

    private static final String TB_DOMAINE_INDICATEURS_JSON_FILE = "tb_domaine_indicateurs.json";
    private static final String TB_DOMAINE_INDICATEURS_DIR = "tb_domaine_indicateurs"; // optional directory containing
                                                                                       // per-domaine json files

    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping TB domaine-indicateur seeding.");
            return;
        }

        try {
            String initDataPath = dataExternalPath + "/init-data/tableau_bord" + "/" + territoire;
            Path resourcePath = Paths.get(initDataPath);
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping TB domaine-indicateur seeding.", resourcePath);
                return;
            }

            // 1) Preferred: directory with per-domaine JSON files
            File dir = new File(resourcePath.toFile(), TB_DOMAINE_INDICATEURS_DIR);
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir
                        .listFiles((d, name) -> d != null && name != null && name.toLowerCase().endsWith(".json"));
                if (files != null && files.length > 0) {
                    log.info("Found {} per-domaine TB indicateur files in {}", files.length, dir.getAbsolutePath());
                    for (File f : files) {
                        attachIndicateursFromDomainFile(f);
                    }
                    return; // done
                } else {
                    log.warn("Directory '{}' exists but contains no JSON files. Falling back to legacy file.",
                            dir.getAbsolutePath());
                }
            }

            // 2) Fallback: single legacy JSON file
            File legacyJson = new File(resourcePath.toFile(), TB_DOMAINE_INDICATEURS_JSON_FILE);
            if (!fileDataService.fileExists(legacyJson)) {
                log.warn(
                        "Neither per-domaine directory nor legacy TB domaine-indicateur JSON file found. Looked at: {} and {}",
                        dir.getAbsolutePath(), legacyJson.getAbsolutePath());
                return;
            }
            log.info("Using legacy TB domaine-indicateur JSON file: {}", legacyJson.getAbsolutePath());
            attachIndicateursFromJson(legacyJson);
        } catch (Exception e) {
            log.error("Error during TB domaine-indicateur seeding: {}", e.getMessage(), e);
        }
    }

    @Transactional
    protected void attachIndicateursFromJson(File jsonFile) {
        try {
            List<Row> rows = fileDataService.readJsonFileAsList(jsonFile, new TypeReference<List<Row>>() {
            });
            if (rows == null || rows.isEmpty()) {
                log.warn("No TB domaine-indicateur rows found in file: {}", jsonFile.getName());
                return;
            }
            processRowsGroupedByDomaine(rows, jsonFile.getName());
        } catch (Exception e) {
            log.error("Error processing TB domaine-indicateur file {}: {}", jsonFile.getName(), e.getMessage(), e);
        }
    }

    @Transactional
    protected void attachIndicateursFromDomainFile(File jsonFile) {
        try {
            List<Row> rows = fileDataService.readJsonFileAsList(jsonFile, new TypeReference<List<Row>>() {
            });
            if (rows == null || rows.isEmpty()) {
                log.warn("Empty domaine indicateur file: {}", jsonFile.getName());
                return;
            }
            processRowsGroupedByDomaine(rows, jsonFile.getName());
        } catch (Exception e) {
            log.error("Error processing per-domaine file {}: {}", jsonFile.getName(), e.getMessage(), e);
        }
    }

    private void processRowsGroupedByDomaine(List<Row> rows, String sourceName) {
        // Group by tbDomaine name (preserve original casing as persisted)
        Map<String, List<Row>> byDomain = rows.stream()
                .collect(Collectors.groupingBy(r -> r.getTbDomaine() == null ? null : r.getTbDomaine().trim()));

        for (Map.Entry<String, List<Row>> entry : byDomain.entrySet()) {
            String tbDomaineName = entry.getKey();
            if (tbDomaineName == null) {
                log.warn("Encountered row with null tbDomaine in source {}. Skipping those rows.", sourceName);
                continue;
            }

            List<Row> domainRows = entry.getValue();

            String normalizedDomaineLookup = normalize(tbDomaineName);
            TBDomaine tbDomaine = tbDomaineService.findByNom(normalizedDomaineLookup.toLowerCase())
                    .orElseGet(() -> {
                        log.warn("TB domaine '{}' (normalized: '{}') not found (source '{}'). Skipping its rows.",
                                tbDomaineName, normalizedDomaineLookup,
                                sourceName);
                        return null;
                    });
            if (tbDomaine == null)
                continue;

            List<TBDomaineIndicateur> existing = tbDomaineIndicateurRepository
                    .findByTBDomaineIdOrderByOrdreAsc(tbDomaine.getId());
            Set<Long> existingIndicateurIds = existing.stream().map(e -> e.getIndicateur().getId())
                    .collect(Collectors.toCollection(HashSet::new));

            int nextOrdre = existing.size();
            List<TBDomaineIndicateurRequestDto> requests = new ArrayList<>();
            Map<String, Long> resolvedCache = new HashMap<>();

            for (Row row : domainRows) {
                String indicateurName = normalize(row.getIndicateur());
                if (indicateurName == null || indicateurName.isBlank()) {
                    continue;
                }

                Long indicateurId = resolvedCache.get(indicateurName);
                if (indicateurId == null) {
                    Indicateur indicator = indicateurService.findByNom(indicateurName.toLowerCase()).orElse(null);
                    if (indicator == null) {
                        // relaxed: strip accents
                        String stripped = stripAccents(indicateurName);
                        if (!stripped.equals(indicateurName)) {
                            indicator = indicateurService.findByNom(stripped.toLowerCase()).orElse(null);
                        }
                    }
                    if (indicator == null) {
                        log.warn("Indicateur '{}' not found (source '{}'). Skipping.", indicateurName, sourceName);
                        continue;
                    }
                    indicateurId = indicator.getId();
                    resolvedCache.put(indicateurName, indicateurId);
                }

                if (existingIndicateurIds.contains(indicateurId)) {
                    log.debug("Association already exists: tbDomaine='{}', indicateur='{}' (source '{}')",
                            tbDomaineName,
                            indicateurName, sourceName);
                    continue;
                }

                TBDomaineIndicateurRequestDto req = new TBDomaineIndicateurRequestDto();
                TBDomaineDto tbDto = new TBDomaineDto();
                tbDto.setId(tbDomaine.getId());
                req.setTbDomaine(tbDto);

                IndicateurDto indDto = new IndicateurDto();
                indDto.setId(indicateurId);
                req.setIndicateur(indDto);

                req.setCategorie(row.getCategorie() != null ? row.getCategorie().toLowerCase().trim() : null);
                req.setOrdre(nextOrdre++);
                requests.add(req);
            }

            if (!requests.isEmpty()) {
                tbDomaineIndicateurService.attachIndicateursToTBDomaine(requests);
                log.info("Attached {} indicateurs to TB domaine '{}' (source '{}')", requests.size(), tbDomaineName,
                        sourceName);
            } else {
                log.info("No new indicateurs to attach for TB domaine '{}' (source '{}')", tbDomaineName, sourceName);
            }
        }
    }

    private static String normalize(String s) {
        if (s == null)
            return null;
        String v = s.trim();
        v = v.replace('\u2019', '\'') // right single quote
                .replace('\u2018', '\'') // left single quote
                .replace("’", "'")
                .replace("‘", "'")
                .replace("`", "'");
        v = v.replaceAll("\\s+", " ");
        return v.toLowerCase();
    }

    private static String stripAccents(String input) {
        if (input == null)
            return null;
        String norm = Normalizer.normalize(input, Normalizer.Form.NFD);
        return norm.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Row {
        private String tbDomaine; // e.g., "Offre de travail"
        private String categorie; // e.g., "activité", "chômage ", "neet"
        private String indicateur; // the indicator name
    }
}
